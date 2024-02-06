package com.example.pixabay.model.api

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pixabay.model.Image
import com.example.pixabay.PixabayRepository.Companion.NETWORK_PAGE_SIZE

private const val PIXABAY_STARTING_PAGE_INDEX = 1

class PixabayPagingSource(
    private val service: PixabayApi, private val query: String
) : PagingSource<Int, Image>() {
    override fun getRefreshKey(state: PagingState<Int, Image>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Image> {
        val position = params.key ?: PIXABAY_STARTING_PAGE_INDEX
        return try {
            val response = service.searchImages(query, position, params.loadSize)
            val hits = response.hits
            val nextKey = if (hits.isEmpty()) {
                null
            } else {
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = hits,
                prevKey = if (position == PIXABAY_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}