package com.example.unsplashimageviewer.api

import com.example.unsplashimageviewer.data.UnsplashPhoto

data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)