package com.example.pixabay.api

import androidx.room.Room
import com.example.pixabay.Image
import com.example.pixabay.db.ImageDao
import com.example.pixabay.db.ImageDataBase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.io.IOException
import java.lang.Exception

val pixabayModule = module {
    single {
        Room.databaseBuilder(androidApplication(), ImageDataBase::class.java, "app_database")
            .build()
    }
    single { get<ImageDataBase>().imageDao() }
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
        imageDao.insertAllImages(images)
    }

    private suspend fun getImagesFromDatabase(query: String): List<Image> {
        return imageDao.getImagesByQuery(query)
    }

}