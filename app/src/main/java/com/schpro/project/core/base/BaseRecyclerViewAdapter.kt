package com.schpro.project.core.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T : Any>(
    @LayoutRes val layoutId: Int,
    inline val bind: (item: T, holder: BaseViewHolder, itemCount: Int, adapter: BaseRecyclerViewAdapter<T>) -> Unit
) : ListAdapter<T, BaseViewHolder>(BaseItemCallback<T>()) {

    override fun getItemViewType(position: Int): Int = layoutId

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bind(getItem(position), holder, itemCount, this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return BaseViewHolder(root as ViewGroup)
    }
}

class BaseViewHolder(container: ViewGroup) : RecyclerView.ViewHolder(container)