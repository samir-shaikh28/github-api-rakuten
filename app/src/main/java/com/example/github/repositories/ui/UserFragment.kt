package com.example.github.repositories.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.github.repositories.R
import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.data.UserDTO
import com.example.github.repositories.databinding.FragmentUserBinding
import com.example.github.repositories.listener.OnRepoListItemClickListener
import com.example.github.repositories.network.*
import com.example.github.repositories.repository.Repository
import com.example.github.repositories.ui.adapters.GenericRecyclerAdapter
import com.example.github.repositories.ui.viewholder.AbstractViewHolder
import com.example.github.repositories.ui.viewholder.RepoListItemViewHolder
import com.example.github.repositories.ui.viewmodels.UserStateViewModel
import com.example.github.repositories.ui.viewmodels.UserViewModel
import com.example.github.repositories.ui.viewmodels.UserViewModelFactory
import com.example.github.repositories.utils.NetworkUtils
import com.example.github.repositories.utils.VerticalSpaceItemDecoration
import com.squareup.picasso.Picasso

class UserFragment : Fragment(), OnRepoListItemClickListener {

    private lateinit var userViewModel: UserViewModel
    private lateinit var userStateViewModel: UserStateViewModel
    private lateinit var mBinding: FragmentUserBinding
    private lateinit var navController: NavController
    private  var userName: String? = null
    private val endPoint by lazy { createRetrofitEndPoint() }
    private val repository by lazy { Repository(endPoint) }
    private val adapter by lazy { GenericRecyclerAdapter() }

    private fun createRetrofitEndPoint(): GitHubEndpoints {
        return ApiClient.retrofitInstance.create(GitHubEndpoints::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        userStateViewModel = ViewModelProvider(requireActivity())[UserStateViewModel::class.java]
        userViewModel =
            ViewModelProvider(this, UserViewModelFactory(repository))[UserViewModel::class.java]
        setUpRecyclerView()
        observeUserData()
        observeUserRepository()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userStateViewModel.selectedRepo.observe(viewLifecycleOwner) {
            userName = it.owner?.login
            fetchUserData()

        }
    }

    private fun showProgressBar() {
        mBinding.progress.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        mBinding.progress.visibility = View.GONE
    }



    private fun observeUserRepository() {
        userViewModel.repositories.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Success -> {
                    hideProgressBar()
                    updateUserRepositories(result.data)
                }
                is Error -> {
                    hideProgressBar()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_text),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Failure -> {
                    hideProgressBar()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateUserRepositories(data: List<RepositoryDTO>) {
        adapter.listCustomModel = getListOfAbstractVH(data)
        adapter.notifyDataSetChanged()
    }

    private fun getListOfAbstractVH(data: List<RepositoryDTO>): List<AbstractViewHolder> {
        val list = mutableListOf<AbstractViewHolder>()
        // As per requirement taking only top 20 repositories
        data.take(20).forEachIndexed { index, item ->
            list.add(RepoListItemViewHolder((index + 1).toString(), item, this@UserFragment))
        }
        return list
    }

    private fun observeUserData() {
        userViewModel.userObserver.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Success -> {
                    handleSuccessResponse(result.data)
                }
                is Error -> {
                    handleError(getString(R.string.error_text))
                }
                is Failure -> {
                    handleError(getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun handleSuccessResponse(data: UserDTO) {
        mBinding.lytError.lytError.visibility = View.GONE
        mBinding.container.visibility = View.VISIBLE
        updateUserData(data)
        fetchUserRepository(data.repos_url)
    }

    private fun updateUserData(data: UserDTO) {
        mBinding.title.text = data.login
        if (data.twitter_username.isNullOrEmpty()) {
            mBinding.detail.visibility = View.GONE
        } else {
            mBinding.detail.text =
                String.format(getString(R.string.twitter_handle), data.twitter_username)
        }
        Picasso.get().load(data.avatar_url?.toUri()).into(mBinding.image)
    }

    private fun fetchUserRepository(reposUrl: String?) {
        if (reposUrl == null) {
            Toast.makeText(
                requireContext(),
                "Can't fetch user repositories, user repo url is null",
                Toast.LENGTH_SHORT
            ).show()
            mBinding.list.visibility = View.GONE
            return
        }
        userViewModel.fetchRepositories(reposUrl)
    }

    private fun setUpRecyclerView() {
        mBinding.list.layoutManager = LinearLayoutManager(requireContext())
        mBinding.list.addItemDecoration(
            VerticalSpaceItemDecoration(
                resources.getDimension(R.dimen.vertical_margin_half).toInt()
            )
        )
        mBinding.list.adapter = adapter
    }


    private fun fetchUserData() {

        if(!NetworkUtils.isNetworkAvailable(requireContext())) {
            handleError(getString(R.string.network_error))
            return
        }
        showProgressBar()
        // Considering we will always get userName from top repositories api
        userViewModel.fetchUser(userName!!)
    }

    private fun handleError(errorMsg: String) {
        hideProgressBar()
        mBinding.container.visibility = View.GONE
        mBinding.lytError.lytError.visibility = View.VISIBLE
        mBinding.lytError.errorText.text = errorMsg
        mBinding.lytError.btnRetry.setOnClickListener {
            fetchUserData()
        }
    }

    override fun openFragmentDetail(repositoryDTO: RepositoryDTO) {
        userStateViewModel.selectedRepo.postValue(repositoryDTO)
        navController.navigate(R.id.action_userFragment_to_detailFragment)
    }
}