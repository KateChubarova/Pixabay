package com.example.pixabay.model


import android.provider.ContactsContract.CommonDataKinds.Im
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.pixabay.model.api.PixabayService
import com.example.pixabay.model.db.ImageDataBase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.HttpException
import java.io.IOException

const val GITHUB_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class ImageRemoteMediator(
    private val query: String
) : RemoteMediator<Int, Image>(), KoinComponent {

    val service: PixabayService by inject()
    val dataBase: ImageDataBase by inject()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Image>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: GITHUB_STARTING_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }

        try {
            val apiResponse = service.searchImages(query, page, state.config.pageSize)

            val images = apiResponse.hits
            val endOfPaginationReached = images.isEmpty()
            dataBase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    dataBase.remoteKeysDao().clearRemoteKeys()
                    dataBase.imageDao().clearImages()
                }
                val prevKey = if (page == GITHUB_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = images.map {
                    RemoteKeys(imageId = it.id, prevKey = prevKey, nextKey = nextKey)
                }


                dataBase.remoteKeysDao().insertAll(keys)
                dataBase.imageDao().insertAllImages(images)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Image>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { image ->
                // Get the remote keys of the last item retrieved
                dataBase.remoteKeysDao().remoteKeysRepoId(image.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Image>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { image ->
                // Get the remote keys of the first items retrieved
                dataBase.remoteKeysDao().remoteKeysRepoId(image.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Image>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { imageId ->
                dataBase.remoteKeysDao().remoteKeysRepoId(imageId)
            }
        }
    }
}