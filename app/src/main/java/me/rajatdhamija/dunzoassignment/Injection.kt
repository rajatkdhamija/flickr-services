package me.rajatdhamija.dunzoassignment

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import me.rajatdhamija.dunzoassignment.api.FlickrService
import me.rajatdhamija.dunzoassignment.data.PhotosRepository
import me.rajatdhamija.dunzoassignment.db.PhotoDatabase
import me.rajatdhamija.dunzoassignment.utils.ViewModelFactory

object Injection {
    private fun provideFlickrRepository(context: Context): PhotosRepository {
        return PhotosRepository(FlickrService.create(), PhotoDatabase.getInstance(context))
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideFlickrRepository(context))
    }
}
