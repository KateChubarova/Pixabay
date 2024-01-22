package com.example.pixabay

import android.app.Application
import com.example.pixabay.api.networkModule
import com.example.pixabay.api.pixabayModule
import com.example.pixabay.db.dbModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PixabayApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PixabayApplication)
            androidLogger()
            modules(networkModule, pixabayModule, viewModelModule, dbModule)
        }
    }
}