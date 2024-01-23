package com.example.pixabay.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pixabay.Image
import com.example.pixabay.PixabayViewModel
import com.example.pixabay.R

class PixabayAdapter(val pixabayViewModel: PixabayViewModel) :
    RecyclerView.Adapter<PixabayAdapter.ViewHolder>() {

    private var data: List<Image> = emptyList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.pixabay_image)
        private val userName: TextView = itemView.findViewById(R.id.pixabay_user)
        private val tags: TextView = itemView.findViewById(R.id.pixabay_tags)

        fun bind(image: Image) {
            Glide.with(itemView)
                .load(image.previewUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(imageView)
            userName.text = image.userName
            tags.text = image.tags

            itemView.setOnClickListener {
                pixabayViewModel.onItemClick(image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position])

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Image>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size
}