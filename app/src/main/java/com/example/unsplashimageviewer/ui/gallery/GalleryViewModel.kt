package com.example.unsplashimageviewer.ui.gallery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.unsplashimageviewer.data.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


// we use a special injection name for view models
// @ViewModelInject deprecated. using @HiltViewModel annotation with the regular @Inject
// constructor
@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: UnsplashRepository
) : ViewModel() {
    // what if we want to be able to search for something else?
    private val currentQuery = MutableLiveData(DEFAULT_QUERY)

    // we want to be able to observe this in our fragment
    val photos = currentQuery.switchMap { queryString ->
        // this cashedIn(viewModelScope) is necessary to cash the live data
        // otherwise we'll crash on rotation because we can't load from the same paging
        // data twice
        repository.getSearchResults(queryString).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }
    companion object {
        private const val DEFAULT_QUERY = "cats"
    }
}