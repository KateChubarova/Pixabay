package com.example.pixabay.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.pixabay.PixabayViewModel
import com.example.pixabay.R
import com.example.pixabay.utils.afterTextChanged
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment(R.layout.fragment_main) {
    private val pixabayViewModel: PixabayViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRV()
        initSearch()
    }

    private fun initRV() {
        val recyclerView: RecyclerView = requireView().findViewById(R.id.recyclerView)
        val adapter = PixabayAdapter(pixabayViewModel)
        recyclerView.adapter = adapter

        pixabayViewModel.apiResult.observe(this.viewLifecycleOwner) { hits ->
            adapter.setData(hits)
        }

        pixabayViewModel.selectedItem.observe(this.viewLifecycleOwner) { image ->
            val dialogFragment = DialogDetailsFragment.newInstance(image)
            dialogFragment.show(childFragmentManager, "DialogDetailsFragment")
        }
    }

    private fun initSearch() {
        val input: TextInputEditText = requireView().findViewById(R.id.searchEditText)
        input.afterTextChanged { text ->
            pixabayViewModel.getImages(text)
        }
    }
}
