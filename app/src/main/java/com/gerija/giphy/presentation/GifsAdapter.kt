package com.gerija.giphy.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gerija.giphy.R
import com.gerija.giphy.data.api.dto.Data
import java.lang.Thread.sleep
import kotlin.concurrent.thread


class GifsAdapter(val context: Context, val gifOnClick: GifOnClick)
    : RecyclerView.Adapter<GifsAdapter.GifsViewHolder>() {

    var gifsList = arrayListOf<Data>()
    set(value) {
        val callBack = GifItemDiffCallBack(gifsList, value)
        val diffResult = DiffUtil.calculateDiff(callBack)
        diffResult.dispatchUpdatesTo(this)
        field = value
     }

    interface GifOnClick{
        fun onClick(gifsList: ArrayList<Data>, position: Int) //для перехода на окно слайдера
        fun deleteItem(position: Int) //для удаления с mainActivity
    }

    inner class GifsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imGifs = itemView.findViewById<ImageView>(R.id.imGif)
        val imDelete = itemView.findViewById<ImageView>(R.id.imDelete)

        init {
            itemView.setOnClickListener {
                gifOnClick.onClick(gifsList, position)
           }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gif_item, parent, false)
        return GifsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GifsViewHolder, position: Int) {
        val itemGifs = gifsList[position]
        val gifs = itemGifs.images?.original?.url

        Glide.with(context).load(gifs).into(holder.imGifs)

        holder.imDelete.setOnClickListener {
            holder.imDelete.setImageResource(R.drawable.ic_delete_precced)
            thread {
                sleep(200)
                gifOnClick.deleteItem(position)
                holder.imDelete.setImageResource(R.drawable.ic_delete_normal)
            }
        }
    }

    override fun getItemCount() = gifsList.size
}