package com.example.unsplashimageviewer.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.unsplashimageviewer.R
import com.example.unsplashimageviewer.data.UnsplashPhoto
import com.example.unsplashimageviewer.databinding.UnsplashItemBinding

class UnsplashPhotoAdapter(private val listener : OnItemClickListener) :
    PagingDataAdapter<UnsplashPhoto, UnsplashPhotoAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {
// expects diffutil item callback, which is an item that knows how to calculate changes between two datasets when we later update
// the recyclerView- between the new data set and old data set. this makes the rv more efficient because if the new list and old list have
// similar items in them, its not necessary for the rv to redraw the whole list on the screen. instead just redraw the ones that
// have actually changed. this item callback figures out which items those are. also need it to be passed into the constructor, so
// it must be statically available. in Kotlin that is done with the companion object


    // to use view binding here, need to create the constructor, and in the recyclerview viewholder it is expecting to be passed
    // the item to inflate, which will be the inflated root of the unsplash_item


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = UnsplashItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position)
// we need to put the data from c urrebt item into the right views inside the item layout, but it's convention to do this in teh
        //view holder class

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }
    inner class PhotoViewHolder(private val binding: UnsplashItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // we want to send this click to the underlying fragment that contains the rv. we don't want to handle it right here,
            // because we can't handle navigation inside the adapter. we'll need to create an interface, just like java.
            binding.root.setOnClickListener{
                // we need to pass the unsplash photo object, and we can get it with the getItem function we used above
                // but we need the position of the viewholder in the rv to know which item
                val position = bindingAdapterPosition

                // adding this check to make sure there is a position (ie it's not animating off of the screen) otherwise
                // the it may try to use the position of something that is no longer there and the app will crash. not doing
                // animations here, but a good check to have
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }

            }
        }

            fun bind(photo: UnsplashPhoto) {
                // this apply block is to allow us to not have to write binding.textView or binding.imageView a whole bunch of times
                binding.apply {
                    Glide.with(itemView)
                        .load(photo.urls.regular)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.ic_error)
                        .into(imageView)

                    textViewUsername.text = photo.user.username
                }
            }

    }

    interface OnItemClickListener {
        fun onItemClick(photo: UnsplashPhoto)
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {
            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem.id == newItem.id // using kotlin short method syntax
            // this is comparing the id's of the photos, so the recyclerview knows that if they both have the same id, they represent
            // the same logical object


            // this needs to return true if the contents within the item has changed, so then the rv knows it's time to refresh
            // the layout of an item
            override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem == newItem
            // we are seeing why we made UnsplashPhoto a data class, because a data class automatically generates an equals method that
            // compares all properties that we declare in the constructor of the data class, and all nested class. so if any of the
            // values changes, areContentsTheSame will return false and the rv will know it needs to update these items
        }
    }


}