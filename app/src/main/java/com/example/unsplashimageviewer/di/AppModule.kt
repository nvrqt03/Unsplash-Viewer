package com.example.unsplashimageviewer.di

import com.example.unsplashimageviewer.api.UnsplashApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// what does this module do? turns this into a dagger module. we won't actually call the provides methods.
// these are only instructions for dagger. when we ask for unsplashApi instance injected, dagger will look
// to see how to create these later, and will look here. We also need to tell dagger what scope we want to use these
// because an object can live in the lifecycle of the activity or fragment. we want to have these objects available
// in the lifetime of the whole app, and singletons of these as well.

// update - ApplicationComponent deprecated, using SingletonComponent now
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // this annotation turns this into a dagger provides method - a
    // method that tells dagger how to create an object - a retrofit object
    // in this case
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(UnsplashApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // how can we pass retrofit into this? since we already told it how it can create the rotrofit object,
    // we can declare the method parameter, and when dagger later wants to use this method to create the
    // unsplashApi interface, it looks for retrofit and sees the provideRetrofit method
    @Provides
    @Singleton
    fun provideUnsplashApi(retrofit: Retrofit): UnsplashApi =
        retrofit.create(UnsplashApi::class.java)
}