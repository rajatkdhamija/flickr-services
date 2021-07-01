package me.rajatdhamija.dunzoassignment.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import me.rajatdhamija.dunzoassignment.api.FlickrService
import me.rajatdhamija.dunzoassignment.db.Photo
import me.rajatdhamija.dunzoassignment.db.PhotoDatabase

class PhotosRepository(
    private val service: FlickrService,
    private val database: PhotoDatabase
) {
    fun getSearchResultStream(query: String): Flow<PagingData<Photo>> {
        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { database.photosDao().getAllPhotos() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = GithubRemoteMediator(
                query,
                service,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}