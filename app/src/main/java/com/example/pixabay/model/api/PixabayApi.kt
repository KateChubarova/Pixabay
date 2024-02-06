package com.example.pixabay.model.api

import com.example.pixabay.model.ImageResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {
    @GET("/api")
    suspend fun searchImages(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") size: Int
    ): ImageResponse
}