package com.example.flightsearch

import android.app.Application
import com.example.flightsearch.di.AppContainer
import com.example.flightsearch.di.AppDataContainer


/*
 * FlightSearchApplication.kt
 * This is the custom Application class responsible for managing global dependencies.
 * It initializes and holds a container for shared dependencies used across the app.
 */

class FlightSearchApplication : Application() {

    // Holds shared dependencies for the entire application
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Initialize the dependency container when the application starts
        container = AppDataContainer(this)
    }
}
