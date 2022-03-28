package com.example.unsplashimageviewer.ui.gallery

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.unsplashimageviewer.R
import com.example.unsplashimageviewer.data.UnsplashPhoto
import com.example.unsplashimageviewer.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.fragment_gallery.progress_bar
import kotlinx.android.synthetic.main.unsplash_photo_load_state_footerr.*

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery), UnsplashPhotoAdapter.OnItemClickListener {

    // since we added the android entry point above, this will be injected by dagger
    // and the view model itslef
    private val viewModel by viewModels<GalleryViewModel>()

    // we are going to instantiate the adapter in our fragment, so we'll need a binding object to access the recyclerView, but we need to
    // be careful here. when you use viewbinding in a fragment, we need to pay special attention. the view of a fragment can be destroyted
    // while the fragment instance itself is still in memory. if this is the caser we have to null out the binding vqariable otherwise
    // it will keep an unecessary reference to the whole view hierarchy which is a memory leak.

    // create
    private var _binding: FragmentGalleryBinding? = null
    // since this is nullable, we would have to use a safe call operator when using. since we wll be using this often, using the safe call
    // operator every time would get annoying. here's how we deal with that
    private val binding get() = _binding!! // we are saying we don't care if binding is null, just return the not nullable type. if something
    // goes wrong and binding is null, it weill throw a null pointer exception. this way we can use the binding variable without using the
    // the safe call oper
    // ator, but it's only valid in the fragment view's lifecycle between onCreateView and onDestroyView. if we tried to
    // use the variable outside of the lifecycle, it would crash - but that might be better becaue we can't use this binding varialbe some-
    //where else outside this lifecycle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentGalleryBinding.bind(view)

        val adapter = UnsplashPhotoAdapter(this)

        binding.apply {
            recycler_view.setHasFixedSize(true)

            // now after creating the photo  load state adapter
            recycler_view.adapter = adapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter { adapter.retry() },
                footer = UnsplashPhotoLoadStateAdapter { adapter.retry() }
            )
            buttonRetry.setOnClickListener {
                adapter.retry()
            }
        }

        // observe photos live data. important to pass viewLifecycleOwner to the live
        // data method and not the fragment itself because we want to sotp updating the
        // ui when the view of the fragment is destroyed. this can happen when the
        // fragment instance itself is still in memory, like when the fragment is put into
        // the backstack. so always put viewLifecycleOwner and not "this"
        viewModel.photos.observe(viewLifecycleOwner) {
            // this is where we use a paging data adapter to update the recyclerViewer
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
            // you want to make sure to call the viewLifeCycleOwner lifecycle and not jsut the lifeCcyle because this is the lifecycle
            // of the view of the fragment, not of the fragment instance itself. othewise when we later add the callback, we can get crashes
            // because the paging library will try to call the binding while the view of the fragment is destroyed. Generally when you use
            // lifecycles and callbackas in fragments, you want to have them scoped to the fragments view becasu this is only the part
            // that ids visible on the screen. the fragtment instance itself is just a container.
        }

        // we are working on getting the progress bar to show when searching, or the textView that shows when the rv is empty
        // meaning no search results
        // loadState is of type CombinedLoadStates which combines the load states of different scenarios into one object. the
        // loadstates for when we refresh the dataset or when we append new data to it. we can use this to check for new
        // make our views visible or invisible
        adapter.addLoadStateListener { loadState ->
            binding.apply {
                // we are refreshing the list with a new dataset, progress bar is visible. otherwise invisible
                progress_bar.isVisible = loadState.source.refresh is LoadState.Loading
                // data is done loading, everything is fine
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading

                // visible if something goes wrong, like no internet connection
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textView_error.isVisible = loadState.source.refresh is LoadState.Error

                // when do we show empty textView?
                if (loadState.source.refresh is LoadState.NotLoading &&
                    // means no more items left to be loaded - end of data
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }

            }

            setHasOptionsMenu(true) // need this to display the options menu in the fragment - won't even see it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_gallery, menu)
        // make reference to the search functino
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView // make sure this is androidx SearchView
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                // search when we press the search button, which calls this textSubmit button and will return true
                // p0 is the search string that we get passed in the menu
                if ( p0 != null) {
                    viewModel.searchPhotos(p0)
                    // remember this is the functino we created earlier which changes the value of the current query
                    // which triggers the switchmatch that executes a new search and this should change the photos liveData
                    // which we observe in the fragment so we should be notified of the update. all we need to do is call
                    // this method and return true as well


                    // we do this here because of diffUtil - if the old dataset and the new data set have a similar item
                    // in them, diffutil may keep the scroll position at this item. with this method we make sure we jump
                    // back to the top of the list of new search items
                    binding.recyclerView.scrollToPosition(0)

                    searchView.clearFocus() // hides the keyboard after we submit the search
                }
                return true;
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                // don't do anything here - we don't want to start searching while we are still typing. just return true
                return true;
            }

        })

    }

    // calling this and setting binding to null to release the reference -  will be released when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(photo: UnsplashPhoto) {
        // remember in the nav_graph we navigated to the details fragment and under arguments made it parcelable. This
        // allows us to send the parameters in a compile time safe way rather than putting in a bundle like java
        val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(photo)
        findNavController().navigate(action)
    }
}