package com.example.github.repositories.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.github.repositories.R
import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.databinding.FragmentMainBinding
import com.example.github.repositories.listener.OnRepoListItemClickListener
import com.example.github.repositories.network.*
import com.example.github.repositories.repository.Repository
import com.example.github.repositories.ui.adapters.GenericRecyclerAdapter
import com.example.github.repositories.ui.viewholder.AbstractViewHolder
import com.example.github.repositories.ui.viewholder.RepoListItemViewHolder
import com.example.github.repositories.ui.viewmodels.MainViewModel
import com.example.github.repositories.ui.viewmodels.MainViewModelFactory
import com.example.github.repositories.ui.viewmodels.UserStateViewModel
import com.example.github.repositories.utils.NetworkUtils
import com.example.github.repositories.utils.VerticalSpaceItemDecoration

class MainFragment : Fragment(), OnRepoListItemClickListener {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var userStateViewModel: UserStateViewModel
    private lateinit var mBinding: FragmentMainBinding
    private lateinit var navController: NavController
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        mainViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(repository)
        )[MainViewModel::class.java]
        userStateViewModel = ViewModelProvider(requireActivity())[UserStateViewModel::class.java]
        setUpRecyclerView()
        handleSwipeRefreshLayout()
        fetchRepositories()
        return mBinding.root
    }

    private fun fetchRepositories() {
        if(!NetworkUtils.isNetworkAvailable(requireContext())) {
            handleError(getString(R.string.network_error))
            return
        }
        showProgress()
        mainViewModel.fetchItems()

    }

    private fun handleError(errorMsg: String) {
        hideProgress()
        mBinding.swipeRefresh.visibility = View.GONE
        mBinding.lytError.lytError.visibility = View.VISIBLE
        mBinding.lytError.errorText.text = errorMsg
        mBinding.lytError.btnRetry.setOnClickListener {
            fetchRepositories()
        }
    }

    private fun handleSwipeRefreshLayout() {
        mBinding.swipeRefresh.setOnRefreshListener {
            mainViewModel.refresh()
        }
    }

    private fun setUpRecyclerView() {
        mBinding.newsList.layoutManager = LinearLayoutManager(requireContext())
        mBinding.newsList.addItemDecoration(
            VerticalSpaceItemDecoration(
                resources.getDimension(R.dimen.vertical_margin_half).toInt()
            )
        )
        mBinding.newsList.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeRepositoryList()
    }

    private fun observeRepositoryList() {
        mainViewModel.repositories.observe(viewLifecycleOwner) { result ->
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

    private fun handleSuccessResponse(data: List<RepositoryDTO>) {
        if (mBinding.swipeRefresh.isRefreshing)
            mBinding.swipeRefresh.isRefreshing = false
        hideProgress()
        mBinding.swipeRefresh.visibility = View.VISIBLE
        mBinding.lytError.lytError.visibility = View.GONE
        updateUi(data)
    }

    private fun hideProgress() {
        mBinding.progressBar.visibility = View.GONE
    }

    private fun showProgress() {
        mBinding.progressBar.visibility = View.VISIBLE
    }

    private fun updateUi(data: List<RepositoryDTO>) {
        adapter.listCustomModel = getListOfAbstractVH(data)
        adapter.notifyDataSetChanged()
    }

    private fun getListOfAbstractVH(data: List<RepositoryDTO>): List<AbstractViewHolder> {
        val list = mutableListOf<AbstractViewHolder>()
        // As per requirement taking only top 20 repositories
        data.take(20).forEachIndexed { index, item ->
            list.add(RepoListItemViewHolder((index + 1).toString(), item, this@MainFragment))
        }
        return list
    }

    override fun openFragmentDetail(repositoryDTO: RepositoryDTO) {
        userStateViewModel.selectedRepo.postValue(repositoryDTO)
        navController.navigate(R.id.action_mainFragment_to_detailFragment)
    }
}