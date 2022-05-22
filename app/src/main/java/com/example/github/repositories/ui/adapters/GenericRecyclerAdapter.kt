package com.example.github.repositories.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.github.repositories.ui.viewholder.AbstractViewHolder
import  com.example.github.repositories.BR

class GenericRecyclerAdapter(var listCustomModel: List<AbstractViewHolder> = listOf()) :
    RecyclerView.Adapter<GenericRecyclerAdapter.ViewHolder>() {



    class ViewHolder(private val bind: ViewDataBinding) : RecyclerView.ViewHolder(bind.root) {
        fun getBinding(): ViewDataBinding {
            return bind
        }
    }

    override fun getItemViewType(position: Int): Int {
        return listCustomModel[position].getLayoutIdentifier()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context), viewType, parent, false
        )
        return ViewHolder(bind)
    }

    override fun getItemCount(): Int {
        return listCustomModel.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.getBinding().setVariable(BR.group, listCustomModel[position])
        holder.getBinding().executePendingBindings()
    }

}