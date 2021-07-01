package me.rajatdhamija.dunzoassignment.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.rajatdhamija.dunzoassignment.data.PhotosRepository
import me.rajatdhamija.dunzoassignment.ui.MainViewModel

/**
 * Factory for ViewModels
 */
class ViewModelFactory(private val repository: PhotosRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
