package com.example.unsplashimageviewer.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.unsplashimageviewer.api.UnsplashApi
import javax.inject.Inject
import javax.inject.Singleton

// we want dagger to inject this, so we use the @Inject constructor, and pass in what
// we want. how can we use the @Inject here an not in our module? this is because we
// own this class. we don't own retrofit, so we wouldn't try to inject an object there
// again we only want one instance of this, so we use the singleton
@Singleton
class UnsplashRepository @Inject constructor(private val unsplashApi: UnsplashApi) {
    // this will be called later by our view model. we want to return a pager
    fun getSearchResults(query: String) =
        Pager(
            config = PagingConfig(
                // if this pageSize is too small, the user will see the progress bar at the
                // bottom if you scroll fast. but if it's too big, loading too much data in
                // phone's memory
                pageSize = 20, // this gets passed to the unsplashPagingSource - params
                maxSize = 100, // we define number of items in recyclerview. we want to
                // limit this to prevent unnecessary use of memory
                enablePlaceholders = false // the paging library can display placeholders
            // to objects that haven't loaded but not using this
            // pagingSourceFactory - pass in instance of paging source, unsplashApi which is injected
            // by dagger into this repo, and query which will be passed dynamically at run time when we
            // make this request from the viewModel
            ), pagingSourceFactory = { UnsplashPagingSource(unsplashApi, query)}
        ).liveData  // we can observe this stream from our fragment to get live updates
// can also use flow here
    // now we call this from viewModel
}