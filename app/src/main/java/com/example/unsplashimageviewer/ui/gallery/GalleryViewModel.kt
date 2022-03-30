package com.example.unsplashimageviewer.ui.gallery

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.unsplashimageviewer.data.UnsplashRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


// we use a special injection name for view models
// @ViewModelInject deprecated. using @HiltViewModel annotation with the regular @Inject
// constructor
@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: UnsplashRepository
//    @Assisted state: SavedStateHandle // <-- ok this is kinda cool. "process death"... explanation below
) : ViewModel() {
    // what if we want to be able to search for something else?
    private val currentQuery = MutableLiveData(DEFAULT_QUERY) // <-- we changed this for process death handling once find workaround
    // private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY) <-- Can't used this yet. need to find workaround
    // there's an error when using @HiltViewModel with @Assisted. "@Assisted parameters can only be used within an
    // @AssistedInject-annotated constructor." you also can't use @AssistedInject with @HiltViewModel

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
        private const val CURRENT_QUERY = "current_query" // this name doesn't matter
        private const val DEFAULT_QUERY = "cats"
    }
}
/* Process Death explanation:
when the user is using our app and puts it into the backgroubnd, the system might decicde to kill the process because there's
not enough system resources to keep it running all the time.

when the user navigates back to the app, he could find himself back to the starting point of the app, or some other wierd scenario
that doesn't make sense. But not handling process death could result in a bad user experience.

can't save a lot of data here. can only save minimal amount. scroll position was saved, but the last search query was lost.

what we ended up doing is passing state of type SaveStateHandle into the constructor because we need the to save pieces of data
through process death then restore them afterwards, and to let dagger inject this, we add the @Assisted annotation. since
we only need the last search query, remove the mutable live data, and we get4 our value from the save state handle. so for the
current query, we set that to state.getLiveData(). we'll need to put a key which we create another constant called CURRENT_QUERY.
the contents don't matter, just needs to be unique. then we pass  it as the first argument, then pass the default query. what
this does is load the last query from the saved state through process death. if there's no value, we use our default value.
changes to the current query, it's automatically persisted in this state, we don't have to add anything else.


 */