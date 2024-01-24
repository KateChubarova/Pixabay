package com.example.pixabay.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pixabay.PixabayViewModel
import com.example.pixabay.R
import com.example.pixabay.databinding.FragmentMainBinding
import com.example.pixabay.utils.calculateNoOfColumns
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MainFragment : Fragment(R.layout.fragment_main) {

    private var binding: FragmentMainBinding? = null
    private val pixabayViewModel: PixabayViewModel by activityViewModel()
    private lateinit var adapter: PixabayAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        initSearch()
        initRV()
        initViewModel()
        setRVLayoutManager()
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initRV() {
        adapter = PixabayAdapter(pixabayViewModel)
        binding?.recyclerView?.adapter = adapter
    }

    private fun initViewModel() {
        pixabayViewModel.apiResult.observe(this.viewLifecycleOwner) { hits ->
            adapter.setData(hits)
        }

        pixabayViewModel.clicked.observe(this.viewLifecycleOwner) { image ->
            image?.let {
                openDialog()
                pixabayViewModel.dialogOpened()
            }
        }

        pixabayViewModel.positiveButtonClickEvent.observe(this.viewLifecycleOwner) { shouldOpenDetails ->
            if (shouldOpenDetails) {
                pixabayViewModel.openDetails()
                openDetails()
            }
        }

        pixabayViewModel.query.observe(this.viewLifecycleOwner) { query ->
            if (query != binding?.searchEditText?.text.toString()) {
                binding?.searchEditText?.setText(query)
            }
        }
    }

    private fun initSearch() {
        binding?.searchEditText?.addTextChangedListener {
            it?.let { editable ->
                pixabayViewModel.updateQuery(editable.toString())
            }
        }
    }

    private fun openDialog() {
        val dialogFragment = DialogDetailsFragment()
        dialogFragment.show(childFragmentManager, "DialogDetailsFragment")
    }

    private fun openDetails() {
        parentFragmentManager.beginTransaction().replace(R.id.container, DetailsFragment())
            .addToBackStack(null).commit()
    }

    private fun setRVLayoutManager() {
        val layoutManager =
            GridLayoutManager(requireContext(), calculateNoOfColumns(requireContext()))
        binding?.recyclerView?.layoutManager = layoutManager
    }
}
