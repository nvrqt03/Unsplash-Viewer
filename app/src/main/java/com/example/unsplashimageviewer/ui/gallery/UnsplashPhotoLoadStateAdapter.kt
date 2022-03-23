package com.example.unsplashimageviewer.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplashimageviewer.databinding.UnsplashPhotoLoadStateFooterrBinding

// the paging 3 library requires an adapter that is only responsible for showing the header and footer
// it uses concat adapter to connect the two adapters (header and footer) into one, so you can display in the same rv
class UnsplashPhotoLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<UnsplashPhotoLoadStateAdapter.LoadStateViewHolder> () {
// this val retry - this is to trigger the retry functionality. we declare a function type, so we will need a function
    // passed in there that returns a Unit (equivalent to void in Java). we will call this in our listener


    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = UnsplashPhotoLoadStateFooterrBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LoadStateViewHolder(binding)
    }

    // this loadState object allows us to see if we're currently loading new items, which we'd want to show the progeress
    // bar, or if there was an error in which case we show the retry button
    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
       holder.bind(loadState)
    }

    // needed to make this an inner class so we are able to access the properties of the other class. This does tightly couple
    // the viewHolder with the adapter, but they belong together logically. the other option would be to put the retry function as
    // an argument in the view holder, but not necessary
    inner class LoadStateViewHolder(private val binding: UnsplashPhotoLoadStateFooterrBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // avoid putting the listener in the onBindViewHolder because it will be repeated over and over again for each item
        // that comes on the screen
        init {
            binding.buttonRetry.setOnClickListener {
                retry.invoke()
            }
        }
        fun bind(loadState: LoadState) {
            // like in the photo adapter, we are going to use the apply block so we don't have to write binding. whatever
            // over and over
            binding.apply {
                // to check the views progress var will be visiblie if its an instance of LoadState.Loading - means we are
                // currently loading new items. if we are not currently loading but the footer or header is still visible
                // something went wrong otherwise the4 footer would disappear
                progressBar.isVisible = loadState is LoadState.Loading
                buttonRetry.isVisible = loadState !is LoadState.Loading // now we need this button to do something. set listener
                textViewError.isVisible = loadState !is LoadState.Loading
            }
        }
    }

}