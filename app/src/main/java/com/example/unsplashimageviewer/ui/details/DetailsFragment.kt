package com.example.unsplashimageviewer.ui.details

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.unsplashimageviewer.R
import com.example.unsplashimageviewer.databinding.FragmentDetailsBinding
import kotlinx.android.synthetic.main.unsplash_item.*

class DetailsFragment: Fragment(R.layout.fragment_details) {
 // here we want to get a handle on the photo we are sending over from the gallery fragment.
    // now we can get our photos from teh args property
    private val args by navArgs<DetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDetailsBinding.bind(view)

        binding.apply {
            val photo = args.photo

            Glide.with(this@DetailsFragment) // when you're in a fragment or activity, you should
            // pass this fragment or activity to glide.with and not the view, as view is less efficient and
            // can cause problems. we use the view inside our adapter because we don't have a reference to the
            // fragment. but here we do.
                .load(photo.urls.full) // we know its very large, but just for the wsake of seeing the progress bar
                .error(R.drawable.ic_error)
                    // we want to have the textview only visible when the image is finished loading
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // set progress bar to invisible in both areas - if request failed error placeholder image
                        // will show
                        progressBar.isVisible = false
                        // must return false in both places or glide will not show the image
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        //
                        textViewCreator.isVisible = true
                        textViewDescription.isVisible = photo.description != null
                        return false
                    }
                    // don't need viiew model for this fragment because we can just get a reference to the
                    //
                })
                .into(detailImageView)

            textViewDescription.text = photo.description

            // we want to create a link to unsplash with the attribution url
            val uri = Uri.parse(photo.user.attributionUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri) // system will whatever it needs here to view the url -
            // normally by opening the browser with this url in it

            textViewCreator.apply {
                text = "Photo by ${photo.user.name} on Unsplash"
                // also want this to be clickable
                setOnClickListener{
                    context.startActivity(intent) // this is an implicit intent
                }
                paint.isUnderlineText = true // will underline this textView
            }
        }
    }
}