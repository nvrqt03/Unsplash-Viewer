package com.example.unsplashimageviewer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


// every app that uses dagger- hilt needs an application class. the only thing we do here
// is annotate, which will trigger code generation that this library needs. we have to
// tell our app that we want to use this class, so we go gto the manifest folder and
// and it by name. also annotate main activity and gallery fragment since they will need
// injection themselves
@HiltAndroidApp
class ImageSearchApplication: Application() {
}