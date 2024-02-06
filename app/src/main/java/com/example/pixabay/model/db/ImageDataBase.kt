package com.example.pixabay.model.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pixabay.model.Image
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dbModule = module {
    single {
        Room.databaseBuilder(androidApplication(), ImageDataBase::class.java, "app_database")
            .build()
    }
    single { get<ImageDataBase>().imageDao() }
}

@Database(entities = [(Image::class)], version = 1)
abstract class ImageDataBase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}