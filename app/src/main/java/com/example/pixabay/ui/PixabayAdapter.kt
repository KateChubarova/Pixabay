package com.example.pixabay.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pixabay.model.Image
import com.example.pixabay.databinding.ItemBinding


class PixabayAdapter(private val openImage: (image: Image) -> Unit) :
    PagingDataAdapter<Image, PixabayAdapter.ViewHolder>(IMAGE_DIFF_CALLBACK) {

    inner class ViewHolder(private val binding: ItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Image) {
            with(binding) {
                Glide.with(itemView)
                    .load(image.previewUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(pixabayImage)

                pixabayUser.text = image.userName
                pixabayTags.text = image.hashTags
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding: ItemBinding =
            ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { image ->
            holder.bind(image)
            holder.itemView.setOnClickListener {
                openImage(image)
            }
        }
    }

    companion object {
        private val IMAGE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Image>() {
            override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean =
                oldItem == newItem
        }
    }
}