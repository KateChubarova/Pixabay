package com.example.pixabay

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "table_image")
data class Image(
    @PrimaryKey
    val id: Int,
    @SerializedName("pageURL")
    val pageUrl: String,
    @SerializedName("user")
    val userName: String,
    @SerializedName("previewURL")
    val previewUrl: String,
    @SerializedName("largeImageURL")
    val largeImageUrl: String,
    val tags: String
)

data class ImageResponse(
    val total: Int,
    val totalHits: Int,
    val hits: List<Image>
)
