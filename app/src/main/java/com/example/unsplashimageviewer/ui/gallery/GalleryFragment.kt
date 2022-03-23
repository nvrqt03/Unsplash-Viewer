package com.example.unsplashimageviewer.ui.gallery

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.unsplashimageviewer.R
import com.example.unsplashimageviewer.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallery.*

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery) {

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

        val adapter = UnsplashPhotoAdapter()

        binding.apply {
            recycler_view.setHasFixedSize(true)
            recycler_view.adapter = adapter
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
    }

    // calling this and setting binding to null to release the reference will be released when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}