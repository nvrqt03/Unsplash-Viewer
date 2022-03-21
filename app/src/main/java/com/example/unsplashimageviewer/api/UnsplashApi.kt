package com.example.unsplashimageviewer.api

import com.example.unsplashimageviewer.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// interface because retrofit will g4enerate implementation for this interaface later, we just
// need to take care of some methods. interfaces don't have mehtod bodies
interface UnsplashApi {

    // this is where you enter the access key

    companion object {

        // by putting constants in the companion object, we're basically making static content. we can call this
        // later by UnsplashApi.BASE_URL
        const val BASE_URL = "https://api.unsplash.com/"
        const val ACCESS_KEY = BuildConfig.APPLICATION_ID
    }
    @Headers("Accept-Version: v1", "Authorization: $ACCESS_KEY")
    @GET("search/photos")
    suspend fun searchPhotos(  // suspend functions don't run on main thread - this is how we handle threading - this
        // can be passed and resumed later without blocking the thread we're on. don't wait for the function to finish.
        // this can only be run by another suspend function, like our paging 3 library
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): UnsplashResponse
}