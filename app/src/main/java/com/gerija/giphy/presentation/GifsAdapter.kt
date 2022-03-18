package com.gerija.giphy.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gerija.giphy.R
import com.gerija.giphy.data.api.dto.Data


class GifsAdapter(val context: Context, val gifOnClick: GifOnClick)
    : ListAdapter<Data, GifsAdapter.GifsViewHolder>(GifItemDiffCallBack()) {

    interface GifOnClick{
        fun onClickItem(data: Data, gifsList: List<Data>, position: Int)
    }

    inner class GifsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imGifs = itemView.findViewById<ImageView>(R.id.imGif)
        private val imDelete = itemView.findViewById<ImageView>(R.id.imDelete)

        init {
            itemView.setOnClickListener {
                gifOnClick.onClickItem(getItem(position), currentList , position)
           }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gif_item, parent, false)
        return GifsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GifsViewHolder, position: Int) {
        val itemGifs = getItem(position)
        val gifs = itemGifs.images.original.url
        Glide.with(context).load(gifs).into(holder.imGifs)
    }
}