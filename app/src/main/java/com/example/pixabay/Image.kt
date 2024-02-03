package com.example.pixabay

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

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
    val tags: String,
    val comments: Int,
    val likes: Int,
    val downloads: Int,
    val imageWidth: Int,
    val imageHeight: Int,
    var query: String = ""
) : Serializable {
    val hashTags: String
        get() = tags.split(", ").map { it.trim() }.joinToString(" ") { "#$it" }
}

data class ImageResponse(
    val total: Int,
    val totalHits: Int,
    val hits: List<Image>
)
