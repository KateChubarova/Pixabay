package com.example.pixabay.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pixabay.Image

@Database(entities = [(Image::class)], version = 1)
abstract class ImageDataBase : RoomDatabase() {
    abstract fun imageDao() : ImageDao
}