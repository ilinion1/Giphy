package com.gerija.giphy.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gerija.giphy.R
import com.gerija.giphy.data.api.dto.Data


class GifsAdapter(val context: Context, val gifOnClick: GifOnClick) : RecyclerView.Adapter<GifsAdapter.GifsViewHolder>() {

    var gifsList = arrayListOf<Data>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    interface GifOnClick{
        fun onClick(data: Data)
    }

    inner class GifsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imGifs = itemView.findViewById<ImageView>(R.id.imGif)
        private val imDelete = itemView.findViewById<ImageView>(R.id.imDelete)

        init {
            itemView.setOnClickListener {
                gifOnClick.onClick(gifsList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gif_item, parent, false)
        return GifsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GifsViewHolder, position: Int) {
        val itemGifs = gifsList[position]
        val gifs = itemGifs.images.original.url
        Glide.with(context).load(gifs).into(holder.imGifs)

    }

    override fun getItemCount() = gifsList.size
}