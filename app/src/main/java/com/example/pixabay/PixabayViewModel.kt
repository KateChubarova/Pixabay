package com.example.pixabay

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

    val apiResult: MutableLiveData<List<Image>> by lazy {
        MutableLiveData()
    }
    val clicked: MutableLiveData<Image?> by lazy {
        MutableLiveData()
    }

    val positiveButtonClickEvent: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }
    val selectedItem: MutableLiveData<Image?> by lazy {
        MutableLiveData()
    }
    val query: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    companion object {
        const val INITIAL_QUERY = "fruit"
    }

    init {
        updateQuery(INITIAL_QUERY)
    }

    fun updateQuery(request: String) {
        if (request != query.value) {
            query.value = request
            getImages()
        }
    }

    private fun getImages() {
        viewModelScope.launch {
            query.value?.let {
                apiResult.postValue(repository.getImages(it))
            }
        }
    }

    fun onItemClick(image: Image?) {
        clicked.postValue(image)
    }

    fun dialogOpened() {
        selectedItem.postValue(clicked.value)
        clicked.postValue(null)
    }

    fun positiveButtonClicked() {
        positiveButtonClickEvent.postValue(true)
    }

    fun openDetails() {
        positiveButtonClickEvent.postValue(false)
    }
}