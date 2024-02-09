package com.example.pixabay

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pixabay.model.Image
import com.example.pixabay.model.ImageRemoteMediator
import com.example.pixabay.model.api.PixabayService
import com.example.pixabay.model.api.PixabayPagingSource
import kotlinx.coroutines.flow.Flow

class PixabayRepository(private val pixabayService: PixabayService) {

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getSearchResultStream(query: String): Flow<PagingData<Image>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = ImageRemoteMediator(
                query
            ),
            pagingSourceFactory = { PixabayPagingSource(pixabayService, query) }
        ).flow
    }
}