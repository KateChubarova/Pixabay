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
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PixabayAdapter
    private lateinit var input: TextInputEditText
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRV()
        initSearch()
    }
    private fun initRV() {
        recyclerView = requireView().findViewById(R.id.recyclerView)
        adapter = PixabayAdapter()
        recyclerView.adapter = adapter

        pixabayViewModel.apiResult.observe(this.viewLifecycleOwner) { hits ->
            adapter.setData(hits)
        }
    }

    private fun initSearch() {
        input = requireView().findViewById(R.id.searchEditText)
        input.afterTextChanged { text ->
            pixabayViewModel.getImages(text)
        }
    }
}
