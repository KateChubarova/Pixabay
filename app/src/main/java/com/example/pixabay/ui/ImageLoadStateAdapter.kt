package com.example.pixabay.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pixabay.R
import com.example.pixabay.databinding.LoadStateFooterViewItemBinding

class ImageLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<ImageLoadStateAdapter.ImageLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: ImageLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ImageLoadStateViewHolder {
        return ImageLoadStateViewHolder.create(parent, retry)
    }

    class ImageLoadStateViewHolder(
        private val binding: LoadStateFooterViewItemBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            with(binding) {
                if (loadState is LoadState.Error) {
                    errorMsg.text = loadState.error.localizedMessage
                }
                progressBar.isVisible = loadState is LoadState.Loading
                retryButton.isVisible = loadState is LoadState.Error
                errorMsg.isVisible = loadState is LoadState.Error
            }
        }

        companion object {
            fun create(parent: ViewGroup, retry: () -> Unit): ImageLoadStateViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.load_state_footer_view_item, parent, false)
                val binding = LoadStateFooterViewItemBinding.bind(view)
                return ImageLoadStateViewHolder(binding, retry)
            }
        }
    }
}