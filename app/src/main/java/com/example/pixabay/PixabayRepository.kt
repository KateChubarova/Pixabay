package com.example.pixabay

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pixabay.model.Image
import com.example.pixabay.model.api.PixabayApi
import com.example.pixabay.model.api.PixabayPagingSource
import com.example.pixabay.model.db.ImageDao
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

class PixabayRepository(private val pixabayApi: PixabayApi, private val imageDao: ImageDao) {

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }

    fun getSearchResultStream(query: String): Flow<PagingData<Image>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PixabayPagingSource(pixabayApi, query) }
        ).flow
    }

    private suspend fun saveImagesToDatabase(images: List<Image>) {
        try {
            imageDao.insertAllImages(images)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun getImagesFromDatabase(query: String): List<Image> =
        imageDao.getImagesByQuery(query)
}