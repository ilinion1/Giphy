package com.gerija.giphy.presentation

import androidx.recyclerview.widget.DiffUtil
import com.gerija.giphy.data.api.dto.Data

class GifItemDiffCallBack(private val oldList: ArrayList<Data>, private val newList: ArrayList<Data>)
    : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].key == newList[newItemPosition].key
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}