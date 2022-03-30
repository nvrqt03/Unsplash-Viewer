package com.example.unsplashimageviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // we need to have the back arrow from the details screen so we can navigate back that way as
    // well as swipe
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // when we use fragment container view, we can't call navController directly from onCreate
    // due to a bug that will crash our app. so we have to do it like this
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.findNavController()

        // this will later connect our app bar to our navigation graph
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        // this will navigate up in our nav graph and return true to indicate it successfully handled
        // the up button. if it returns false for some reason, it will call the default implementation
        return navController.navigateUp() || super.onSupportNavigateUp()

        // you'll notice when we did this, the title changes to something ugly like fragment_details or
        // fragment_gallery. reson it's visible now is because we've connected our action bar to the nav
        // graph. So in the nav_graph.xml, change the label of the gallery fragment to Gallery, and details
        // to photo. you can do this in the design view or xml
    }
}