package com.example.pixabay.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.pixabay.PixabayViewModel
import com.example.pixabay.R
import com.example.pixabay.utils.afterTextChanged
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val pixabayViewModel: PixabayViewModel by viewModel()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PixabayAdapter
    private lateinit var input: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRV()
        initSearch()
    }

    private fun initRV() {
        recyclerView = findViewById(R.id.recyclerView)
        adapter = PixabayAdapter()
        recyclerView.adapter = adapter

        pixabayViewModel.apiResult.observe(this) { hits ->
            adapter.setData(hits)
        }
    }

    private fun initSearch() {
        input = findViewById(R.id.searchEditText)
        input.afterTextChanged { text ->
            pixabayViewModel.getImages(text)
        }
    }
}