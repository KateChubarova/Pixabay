package com.example.pixabay.api

import com.example.pixabay.ImageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {
    @GET("/api")
    suspend fun getImages(@Query("q") query: String): Response<ImageResponse>
}