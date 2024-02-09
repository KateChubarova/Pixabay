package com.example.pixabay.model.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pixabay.model.Image
import com.example.pixabay.model.RemoteKeys
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dbModule = module {
    single {
        Room.databaseBuilder(androidApplication(), ImageDataBase::class.java, "app_database")
            .build()
    }
    single { get<ImageDataBase>().imageDao() }
    single { get<ImageDataBase>().remoteKeysDao() }
}

@Database(
    entities = [Image::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class ImageDataBase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}