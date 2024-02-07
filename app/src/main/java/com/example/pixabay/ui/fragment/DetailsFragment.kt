package com.example.pixabay.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pixabay.PixabayViewModel
import com.example.pixabay.R
import com.example.pixabay.databinding.FragmentDetailsBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val pixabayViewModel: PixabayViewModel by activityViewModel()
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image = pixabayViewModel.selectedItem.value ?: return

        val resources = requireContext().resources
        with(binding) {
            textComments.text = resources.getQuantityString(R.plurals.comments, image.comments, image.comments)
            textLikes.text = resources.getQuantityString(R.plurals.likes, image.likes, image.likes)
            textUserName.text = image.userName
            textDownloads.text =
                resources.getQuantityString(R.plurals.downloads, image.downloads, image.downloads)
            textTags.text = image.hashTags

            Glide.with(requireContext())
                .load(image.largeImageUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(imageView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}