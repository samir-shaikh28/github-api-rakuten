package com.example.github.repositories.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.github.repositories.R
import com.example.github.repositories.data.LocalDataStore
import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.databinding.FragmentDetailBinding
import com.example.github.repositories.ui.viewmodels.UserStateViewModel
import com.squareup.picasso.Picasso

class DetailFragment : Fragment() {

    private lateinit var mBinding: FragmentDetailBinding
    private lateinit var userStateViewModel: UserStateViewModel
    private lateinit var navController: NavController
    val isBookMarked = ObservableBoolean(false)


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        userStateViewModel = ViewModelProvider(requireActivity())[UserStateViewModel::class.java]
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        updateUi()
        return mBinding.root
    }


    private fun handleBookMark(repositoryDTO: RepositoryDTO) {
        mBinding.bookmarkImg.setOnClickListener {
            isBookMarked.set(!isBookMarked.get())
            updateBookMarkUi(isBookMarked.get())
            // Considering Id will not be nul in any case
            LocalDataStore.bookmarkRepo(repositoryDTO.id!!, isBookMarked.get())
        }
    }


    private fun updateBookMarkUi(isBookMark: Boolean) {
        if (isBookMark) {
            mBinding.bookmarkImg.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.baseline_bookmark_black_24
                )
            )
        } else {
            mBinding.bookmarkImg.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.baseline_bookmark_border_black_24
                )
            )
        }
    }


    private fun updateUi() {
        userStateViewModel.selectedRepo.observe(viewLifecycleOwner) {
            isBookMarked.set(LocalDataStore.getBookmarks().contains(it.id))
            updateBookMarkUi(isBookMarked.get())

            handleBookMark(it)

            mBinding.title.text = it.name
            mBinding.detail.text =
                String.format(getString(R.string.repo_detail), it.owner?.login, it.created_at)

            Picasso.get()
                .load(it.owner?.avatar_url)
                .error(R.drawable.caution)
                .placeholder(R.drawable.progress_animation)
                .into(mBinding.avatar)

            mBinding.description.text = it.description
            mBinding.url.text = it.html_url
            mBinding.url.setOnClickListener { v ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.html_url)))
            }
            mBinding.detail.setOnClickListener {
                navController.navigate(R.id.action_detailFragment_to_userFragment)
            }
        }
    }
}