package com.example.unsplashimageviewer.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplashimageviewer.api.UnsplashApi
import retrofit2.HttpException
import java.io.IOException

// not using dagger here because the query is something we don't know at compile time, so
// so we have to pass this in dynamically

private const val UNSPLASH_STARTING_PAGE_INDEX = 1;
// Why aren't we putting this constant in an companion object, is because the starting page
// index is not related to the unsplash paging source class. we want to use it here, but
// it doesn't belong to it.
class UnsplashPagingSource(
    private val unsplashApi: UnsplashApi,
    private val query: String
): PagingSource<Int, UnsplashPhoto>() { // first is key we want to distinguish between the differnet pages.
    // the second argument is the type of data we want to fill the pages. so this will be the unsplash photo
    // objects
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        // this is the function that will trigger the api request and turn the data into
        // pages. we will never call this load function. the paging 3 library takes care of this, and also
        // takes care of launching the correct coroutine. but we need to know what page we're currently on
        val position = params.key?: UNSPLASH_STARTING_PAGE_INDEX // null for first page, so we need to declare what page
        // we want to load. also not hard coding so we declare a constant

        // remember how searchPhotos is a suspend function, and see above - load is also
        // a suspend function. these won't actually be called by us, the paging library
        // will take care of these. we just need to declare them
        // this is the api call
        return try {
            val response = unsplashApi.searchPhotos(query, position, params.loadSize)

            // remember the search photos method doesn't return a list of unsplash photo objects directly
            // we had to create this results envelope class to
            val photos = response.results

            LoadResult.Page(
                data = photos,
                prevKey = if (position == UNSPLASH_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
        // thrown when no internet connection for example
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            // made request but something wrong on server, liek no data at endpoint or
            // bad authentication
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>): Int? {
        return state.anchorPosition
    }

}