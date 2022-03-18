package com.gerija.giphy.presentation

import androidx.recyclerview.widget.DiffUtil
import com.gerija.giphy.data.api.dto.Data

class GifItemDiffCallBack : DiffUtil.ItemCallback<Data>() {
    override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
        return oldItem == newItem
    }
}