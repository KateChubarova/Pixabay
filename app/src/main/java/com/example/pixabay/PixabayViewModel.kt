package com.example.pixabay

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pixabay.model.Image
import com.example.pixabay.model.UiAction
import com.example.pixabay.model.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val LAST_SEARCH_QUERY = "fruit"
const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
const val DEFAULT_QUERY: String = "fruit"

class PixabayViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val pixabayRepository: PixabayRepository
) : ViewModel() {

    val clicked: MutableLiveData<Image?> by lazy {
        MutableLiveData()
    }

    val positiveButtonClickEvent: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val selectedItem: MutableLiveData<Image?> by lazy {
        MutableLiveData()
    }

    val state: StateFlow<UiState>

    val pagingDataFlow: Flow<PagingData<Image>>

    val accept: (UiAction) -> Unit

    init {
        val initialQuery: String = savedStateHandle[LAST_SEARCH_QUERY] ?: DEFAULT_QUERY
        val lastQueryScrolled: String = savedStateHandle[LAST_QUERY_SCROLLED] ?: DEFAULT_QUERY

        val actionStateFlow = MutableSharedFlow<UiAction>()

        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            //onStart - это оператор, который применяется к потоку данных в Kotlin, чтобы
            // определить действия, которые должны быть выполнены в момент начала работы потока.
            .onStart { emit(UiAction.Search(query = initialQuery)) } //startWith()

        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            //distinctUntilChanged - это оператор, который применяется к потоку данных
            // в Kotlin (например, к потоку Flow или RxJava Observable) и удаляет повторяющиеся элементы,
            // сохраняя только уникальные значения. Однако, в отличие от простого удаления всех
            // повторяющихся элементов, distinctUntilChanged оставляет только те элементы, которые
            // отличаются от предыдущего элемента.
            .distinctUntilChanged()
            // shareIn - это оператор распространения (sharing operator) в Kotlin, который
            // применяется к потоку данных, такому как Flow, и позволяет совместно использовать
            // результаты этого потока между несколькими подписчиками.
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }

        pagingDataFlow = searches
            .flatMapLatest { searchImages(queryString = it.query) }
            //В контексте операций на потоках (Flow) в Kotlin, терминальный оператор - это
            //оператор, который завершает последовательность операций и начинает выполнение потока.
            // Такой оператор приводит к тому, что поток начинает свою работу, выполняя все промежуточные
            // операции, которые были применены к нему.
        .cachedIn(viewModelScope)

        state = combine(
            searches,
            queriesScrolled,
            ::Pair
        ).map { (search, scroll) ->
            UiState(
                query = search.query,
                lastQueryScrolled = scroll.currentQuery,
                // If the search query matches the scroll query, the user has scrolled
                hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            //указывает, что состояние будет активным, пока есть подписчики, с таймаутом в 5 секунд.
            //Если в течение 5 секунд после того, как последний подписчик отписался от состояния,
            // новых подписчиков не появится, состояние станет неактивным. Это означает, что состояние
            // больше не будет обновляться, даже если его значение изменится.
            initialValue = UiState()
        )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun searchImages(queryString: String): Flow<PagingData<Image>> =
        pixabayRepository.getSearchResultStream(queryString)

    fun onItemClick(image: Image) {
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

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }
}