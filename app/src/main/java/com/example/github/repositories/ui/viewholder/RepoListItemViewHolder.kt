package com.example.github.repositories.ui.viewholder

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import com.example.github.repositories.R
import com.example.github.repositories.data.LocalDataStore
import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.listener.OnRepoListItemClickListener

class RepoListItemViewHolder(
    val index: String,
    val repoItem: RepositoryDTO,
    private val listener: OnRepoListItemClickListener?
) : AbstractViewHolder() {

    val isBookMarked = ObservableBoolean(false)

    init {
        isBookMarked.set(LocalDataStore.getBookmarks().contains(repoItem.id))
    }

    override fun getLayoutIdentifier(): Int {
        return R.layout.item
    }

    fun onItemClick(v: View) {
        listener?.openFragmentDetail(repoItem)
    }

    fun onBookMarkClick(v: View) {
        isBookMarked.set(!isBookMarked.get())
        // Considering Id will not be nul in any case
        LocalDataStore.bookmarkRepo(repoItem.id!!, isBookMarked.get())
    }


    companion object {

        @JvmStatic
        @BindingAdapter("handleText")
        fun handleText(textView: TextView, group: RepoListItemViewHolder?) {
            group?.repoItem?.description?.let {
                textView.text = handleDescription(it)
            } ?: run {
                textView.visibility = View.GONE
            }
        }

        fun handleDescription(desc: String): String {
            return if (desc.length >= 150) {
                "${desc.substring(0, 150)}..."
            } else {
                desc
            }
        }


    }

}