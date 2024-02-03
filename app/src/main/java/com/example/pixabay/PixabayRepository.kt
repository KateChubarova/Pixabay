package com.example.pixabay

import com.example.pixabay.api.PixabayApi
import com.example.pixabay.db.ImageDao
import org.koin.dsl.module
import java.io.IOException
import java.lang.Exception

val pixabayModule = module {
    factory { PixabayRepository(get(), get()) }
}

class PixabayRepository(private val pixabayApi: PixabayApi, private val imageDao: ImageDao) {
    suspend fun getImages(query: String): List<Image> {
        try {
            val imagesFromApi = getImagesFromApi(query)
            saveImagesToDatabase(imagesFromApi)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getImagesFromDatabase(query)
    }

    private suspend fun getImagesFromApi(query: String): List<Image> {
        val response = pixabayApi.getImages(query)
        if (response.isSuccessful) {
            val hits = response.body()?.hits
            hits?.map { it.query += "$query, " }
            return hits ?: emptyList()
        } else {
            throw IOException("Network request failed")
        }
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