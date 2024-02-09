package com.example.pixabay.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    val imageId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)