package com.example.unsplashimageviewer.data

import kotlinx.android.parcel.Parcelize

// will need to implement parcelable because we'll need to send this whole object to detail screen
@Parcelize
data class UnsplashPhoto(
    val id: String,
    val description: String?,
    val urls: UnsplashPhotoUrls,
    val user: UnsplashUser
) {
    @Parcelize
    data class UnsplashPhotoUrls(
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String,
    )
    @Parcelize
    data class UnsplashUser (
        val name: String,
        val username: String
        )
    {
        val attributionUrl get() = "https://unsplash.com/$username?utm_source=ImageViewer&utm_medium-referral"
    }
}