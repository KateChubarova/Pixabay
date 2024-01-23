package com.example.pixabay.api

import com.example.pixabay.Image
import com.example.pixabay.db.ImageDao
import org.koin.dsl.module
import java.io.IOException
import java.lang.Exception

val pixabayModule = module {
    factory { PixabayRepository(get(), get()) }
}

class PixabayRepository(private val pixabayApi: PixabayApi, private val imageDao: ImageDao) {
    suspend fun getImages(query: String): List<Image> {
        return try {
            val imagesFromApi = getImagesFromApi(query)
            saveImagesToDatabase(imagesFromApi)
            imagesFromApi
        } catch (e: Exception) {
            getImagesFromDatabase(query)
        }
    }

    private suspend fun getImagesFromApi(query: String): List<Image> {
        val response = pixabayApi.getImages(query)
        if (response.isSuccessful) {
            return response.body()?.hits ?: emptyList()
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

    private suspend fun getImagesFromDatabase(query: String): List<Image> {
        return imageDao.getImagesByQuery(query)
    }
}