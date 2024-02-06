package com.example.pixabay.model

import com.example.pixabay.DEFAULT_QUERY

//With Paging 3 we don't need to convert our Flow to LiveData anymore
sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)
