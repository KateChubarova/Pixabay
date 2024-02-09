package com.example.pixabay.model.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pixabay.model.Image

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllImages(post: List<Image>)

    @Query("SELECT * FROM table_image WHERE userName LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun getImagesByQuery(query: String): PagingSource<Int, Image>

    @Query("DELETE FROM table_image")
    suspend fun clearImages()
}