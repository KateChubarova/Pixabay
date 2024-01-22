package com.example.pixabay.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pixabay.Image
import com.example.pixabay.R

class PixabayAdapter(private var data: List<Image>? = null) :
    RecyclerView.Adapter<PixabayAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.pixabay_image)
        val userName: TextView = itemView.findViewById(R.id.pixabay_user)
        val tags: TextView = itemView.findViewById(R.id.pixabay_tags)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data?.get(position)?.let { currentItem ->
            Glide.with(holder.itemView)
                .load(currentItem.previewUrl)
                .centerCrop()
                .into(holder.image)
            holder.userName.text = currentItem.userName
            holder.tags.text = currentItem.tags
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Image>?) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data?.size ?: 0
}