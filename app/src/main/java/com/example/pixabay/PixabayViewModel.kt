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
    private val _clickedItem = MutableLiveData<Image?>()
    val clicked: LiveData<Image?> get() = _clickedItem
    private val _positiveButtonClickEvent = MutableLiveData<Boolean>()
    val positiveButtonClickEvent: LiveData<Boolean> get() = _positiveButtonClickEvent
    private val _selectedItem = MutableLiveData<Image?>()
    val selectedItem: LiveData<Image?> get() = _selectedItem
    private val _query = MutableLiveData<String>().apply {
        value = "fruit"
    }

    init {
        _query.value?.let {
            getImages()
        }
    }

    fun updateQuery(request: String) {
        if (request != _query.value) {
            _query.value = request
            getImages()
        }
    }

    private fun getImages() {
        viewModelScope.launch {
            _query.value?.let {
                _apiResult.postValue(repository.getImages(it))
            }
        }
    }

    fun onItemClick(image: Image?) {
        _clickedItem.postValue(image)
    }

    fun dialogOpened() {
        _selectedItem.postValue(_clickedItem.value)
        _clickedItem.postValue(null)
    }

    fun positiveButtonClicked() {
        _positiveButtonClickEvent.postValue(true)
    }

    fun openDetails() {
        _positiveButtonClickEvent.postValue(false)
    }
}