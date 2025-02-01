package com.example.flightsearch.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.flightsearch.data.FlightSearchDatabase
import com.example.flightsearch.repository.FlightSearchRepository
import com.example.flightsearch.repository.FlightSearchRepositoryImp

/*
 * AppContainer.kt
 * This file defines the application-wide dependencies using the AppContainer interface.
 * It includes the setup for DataStore and the repository instance for managing flight-related data.
 */

// Name of the DataStore file where key-value preferences will be stored.
private const val DATASTORE_NAME = "flight_preferences"

// Extension property to initialize and provide access to DataStore.
// This creates a single instance of DataStore that can be accessed from any part of the app.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

// Interface defining the shared dependencies available throughout the application.
interface AppContainer {
    // Repository instance for managing flight search data.
    val flightSearchRepository: FlightSearchRepository
}

// Concrete implementation of AppContainer that provides actual dependencies.
class AppDataContainer(private val context: Context) : AppContainer {

    // Provides application-level access to the DataStore instance.
    private val dataStore = context.dataStore

    // Lazily initialized FlightSearchRepository to ensure only one instance is created and used.
    override val flightSearchRepository: FlightSearchRepository by lazy {
        FlightSearchRepositoryImp(
            FlightSearchDatabase.getDatabase(context).airportDao(), // Data Access Object (DAO) for airports.
            FlightSearchDatabase.getDatabase(context).favoriteDao(), // DAO for managing favorite routes.
            dataStore // DataStore instance for handling user preferences.
        )
    }
}

