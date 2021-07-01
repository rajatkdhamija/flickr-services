package me.rajatdhamija.dunzoassignment.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import me.rajatdhamija.dunzoassignment.api.FlickrService
import me.rajatdhamija.dunzoassignment.db.Photo
import me.rajatdhamija.dunzoassignment.db.PhotoDatabase
import me.rajatdhamija.dunzoassignment.db.RemoteKeys
import retrofit2.HttpException
import java.io.IOException

private const val FLICKR_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class GithubRemoteMediator(
    private val query: String,
    private val service: FlickrService,
    private val photoDatabase: PhotoDatabase
) : RemoteMediator<Int, Photo>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Photo>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: FLICKR_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }
        try {
            val apiResponse = service.getData(
                "flickr.photos.search",
                "062a6c0c49e4de1d78497d13a7dbb360",
                query, "json", 1, state.config.pageSize, page
            )
            val photos = apiResponse.photos
            val endOfPaginationReached = photos.photos.isEmpty()
            photoDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    photoDatabase.remoteKeysDao().clearRemoteKeys()
                    photoDatabase.photosDao().clearPhotos()
                }
                val prevKey = if (page == FLICKR_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = photos.photos.map {
                    RemoteKeys(photoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                photoDatabase.remoteKeysDao().insertAll(keys)
                photoDatabase.photosDao().insertAll(photos.photos)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Photo>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { photo ->
                photoDatabase.remoteKeysDao().remoteKeysPhotoId(photo.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Photo>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { photo ->
                photoDatabase.remoteKeysDao().remoteKeysPhotoId(photo.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Photo>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { photoId ->
                photoDatabase.remoteKeysDao().remoteKeysPhotoId(photoId)
            }
        }
    }
}
