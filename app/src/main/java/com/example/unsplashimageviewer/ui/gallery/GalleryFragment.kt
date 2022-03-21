package com.example.unsplashimageviewer.ui.gallery

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.unsplashimageviewer.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    // since we added the android entry point above, this will be injected by dagger
    // and the view model itslef
    private val viewModel by viewModels<GalleryViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // observe photos live data. important to pass viewLifecycleOwner to the live
        // data method and not the fragment itself because we want to sotp updating the
        // ui when the view of the fragment is destroyed. this can happen when the
        // fragment instance itself is still in memory, like when the fragment is put into
        // the backstack. so always put viewLifecycleOwner and not "this"
        viewModel.photos.observe(viewLifecycleOwner) {
            // this is where we use a paging data adapter to update the recyclerViewer
        }
    }
}