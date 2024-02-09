package com.example.pixabay

import androidx.lifecycle.SavedStateHandle
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory { PixabayRepository(get()) }
    viewModel { (handle: SavedStateHandle) -> PixabayViewModel(handle, get()) }
}

