package me.rajatdhamija.dunzoassignment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rajatdhamija.dunzoassignment.data.PhotosRepository
import me.rajatdhamija.dunzoassignment.db.Photo

class MainViewModel(private val repository: PhotosRepository) : ViewModel() {
    private var currentQueryValue: String? = null

    private var currentSearchResult: Flow<PagingData<UiModel>>? = null
    fun searchPhoto(queryString: String): Flow<PagingData<UiModel>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult: Flow<PagingData<UiModel>> = repository.getSearchResultStream(queryString)
            .map { pagingData -> pagingData.map { UiModel.PhotoItem(it) } }
            .map {
                it.insertSeparators<UiModel.PhotoItem, UiModel> { before, after ->
                    null
                }
            }
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }
}

sealed class UiModel {
    data class PhotoItem(val photo: Photo) : UiModel()
    data class SeparatorItem(val description: String) : UiModel()
}

private val UiModel.PhotoItem.round: Int
    get() = this.photo.isFriend
