package com.example.pixabay.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pixabay.Image

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllImages(post: List<Image>)

    @Query("SELECT * FROM table_image WHERE userName LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    suspend fun getImagesByQuery(query: String): List<Image>
}