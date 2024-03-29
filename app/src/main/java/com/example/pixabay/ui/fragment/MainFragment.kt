package com.example.pixabay.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pixabay.model.Image
import com.example.pixabay.PixabayViewModel
import com.example.pixabay.R
import com.example.pixabay.databinding.FragmentMainBinding
import com.example.pixabay.model.UiAction
import com.example.pixabay.model.UiState
import com.example.pixabay.ui.DialogDetailsFragment
import com.example.pixabay.ui.ImageLoadStateAdapter
import com.example.pixabay.ui.PixabayAdapter
import com.example.pixabay.utils.RemotePresentationState
import com.example.pixabay.utils.asRemotePresentationState
import com.example.pixabay.utils.calculateNoOfColumns
import com.example.pixabay.utils.setSearchText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val pixabayViewModel: PixabayViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bindState(
            uiState = pixabayViewModel.state,
            pagingData = pixabayViewModel.pagingDataFlow,
            uiActions = pixabayViewModel.accept
        )

        initViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun FragmentMainBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Image>>,
        uiActions: (UiAction) -> Unit
    ) {
        val pixabayAdapter = PixabayAdapter { image ->
            pixabayViewModel.onItemClick(image)
        }
        val footerAdapter = ImageLoadStateAdapter { pixabayAdapter.retry() }
        recyclerView.adapter = pixabayAdapter.withLoadStateFooter(
            footer = footerAdapter
        )
        setRVLayoutManager(pixabayAdapter, footerAdapter)
        retryButton.setOnClickListener { pixabayAdapter.retry() }
        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            pixabayAdapter = pixabayAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    private fun FragmentMainBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchEditText.addTextChangedListener {
            updateImagesFromInput(onQueryChanged)
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(searchEditText::setSearchText)
        }
    }

    private fun FragmentMainBinding.updateImagesFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchEditText.text?.let {
            if (!it.isNullOrEmpty()) {
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun FragmentMainBinding.bindList(
        pixabayAdapter: PixabayAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Image>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = pixabayAdapter.loadStateFlow
            .asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        ).distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest {
                pixabayAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) recyclerView.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            pixabayAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && pixabayAdapter.itemCount == 0
                emptyList.isVisible = isListEmpty
                recyclerView.isVisible = !isListEmpty
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                retryButton.isVisible = loadState.source.refresh is LoadState.Error

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun initViewModel() {
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
    }

    private fun openDialog() {
        val dialogFragment = DialogDetailsFragment()
        dialogFragment.show(childFragmentManager, "DialogDetailsFragment")
    }

    private fun openDetails() {
        parentFragmentManager.beginTransaction().replace(R.id.container, DetailsFragment())
            .addToBackStack(null).commit()
    }

    private fun setRVLayoutManager(adapter: PixabayAdapter, footerAdapter: ImageLoadStateAdapter) {
        val columns = calculateNoOfColumns(requireContext())
        val layoutManager =
            GridLayoutManager(requireContext(), calculateNoOfColumns(requireContext()))
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == adapter.itemCount && footerAdapter.itemCount > 0) {
                    columns
                } else {
                    1
                }
            }

        }
        binding.recyclerView.layoutManager = layoutManager
    }
}
