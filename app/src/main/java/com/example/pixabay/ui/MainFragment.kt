package com.example.pixabay.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pixabay.PixabayViewModel
import com.example.pixabay.R
import com.example.pixabay.databinding.FragmentMainBinding
import com.example.pixabay.utils.afterTextChanged
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val pixabayViewModel: PixabayViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRV()
        initSearch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRV() {
        val adapter = PixabayAdapter(pixabayViewModel)
        binding.recyclerView.adapter = adapter

        pixabayViewModel.apiResult.observe(this.viewLifecycleOwner) { hits ->
            adapter.setData(hits)
        }

        pixabayViewModel.selectedItem.observe(this.viewLifecycleOwner) { image ->
            image?.let {
                val dialogFragment = DialogDetailsFragment()
                dialogFragment.show(childFragmentManager, "DialogDetailsFragment")
            }
        }

        pixabayViewModel.positiveButtonClickEvent.observe(this.viewLifecycleOwner) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, DetailsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun initSearch() {
        binding.searchEditText.afterTextChanged { text ->
            pixabayViewModel.getImages(text)
        }
    }
}
