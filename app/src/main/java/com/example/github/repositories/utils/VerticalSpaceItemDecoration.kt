package com.example.github.repositories.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class VerticalSpaceItemDecoration(private var mVerticalSpaceHeight: Int) :
    RecyclerView.ItemDecoration() {
    fun setmVerticalSpaceHeight(mVerticalSpaceHeight: Int) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = mVerticalSpaceHeight
    }
}
