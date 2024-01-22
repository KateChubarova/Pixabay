package com.example.pixabay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixabay.api.PixabayRepository
import kotlinx.coroutines.launch
import org.koin.dsl.module

val viewModelModule = module {
    factory { PixabayViewModel(get()) }
}

class PixabayViewModel(private val repository: PixabayRepository) : ViewModel() {

    private val _apiResult = MutableLiveData<List<Image>>()
    val apiResult: LiveData<List<Image>> get() = _apiResult

    init {
        getImages("fruit")
    }

    fun getImages(query: String) {
        viewModelScope.launch {
            _apiResult.postValue(repository.getImages(query))
        }
    }
}