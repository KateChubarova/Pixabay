package com.example.pixabay

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pixabay.model.Image
import com.example.pixabay.model.ImageRemoteMediator
import com.example.pixabay.model.db.ImageDataBase
import kotlinx.coroutines.flow.Flow

class PixabayRepository(private val database: ImageDataBase) {

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getSearchResultStream(query: String): Flow<PagingData<Image>> {

        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { database.imageDao().getImagesByQuery(dbQuery) }

        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = ImageRemoteMediator(
                query
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}