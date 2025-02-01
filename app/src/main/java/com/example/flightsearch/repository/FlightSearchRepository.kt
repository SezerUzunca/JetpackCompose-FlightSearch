package com.example.flightsearch.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.AirportDao
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.FavoriteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/*
 * FlightSearchRepository.kt
 * This file defines the repository interface and its implementation
 * for managing flight-related data such as airports, favorite routes,
 * and user search queries.
 */

// Interface defining the contract for data operations related to flights
interface FlightSearchRepository {

    // Retrieves a list of all airports from the database as a Flow.
    // The Flow emits the latest data whenever there are changes in the database.
    fun getAllAirports(): Flow<List<Airport>>

    // Retrieves a filtered list of airports based on a search query as a Flow.
    // The Flow emits a list of airports that match the given query by name or IATA code.
    fun getAirportsByQuery(query: String): Flow<List<Airport>>

    // Retrieves a list of all favorite routes as a Flow.
    // The Flow emits updated data whenever the favorite list changes.
    fun getAllFavorites(): Flow<List<Favorite>>

    // Adds a favorite flight route to the database.
    // This operation is performed asynchronously using coroutines.
    suspend fun insertFavorite(favorite: Favorite)

    // Removes a favorite flight route from the database.
    // If the route does not exist, the operation has no effect.
    suspend fun deleteFavorite(favorite: Favorite)

    // Checks if a specific flight route is marked as a favorite.
    // Returns true if the route is in the favorites list, false otherwise.
    suspend fun isFavorite(departureCode: String, destinationCode: String): Boolean

    // Saves the user's search query into the DataStore.
    // The saved query persists across app restarts.
    suspend fun saveSearchQuery(query: String)

    // Retrieves the saved search query from the DataStore as a Flow.
    // The Flow ensures real-time updates when the query changes.
    suspend fun getSavedSearchQuery(): Flow<String>
}

// Implementation of FlightSearchRepository, providing actual data operations.
class FlightSearchRepositoryImp(
    private val airportDao: AirportDao, // Data Access Object for airport-related queries
    private val favoriteDao: FavoriteDao, // Data Access Object for favorite routes
    private val dataStore: DataStore<Preferences>, // DataStore for persisting user preferences
) : FlightSearchRepository {

    // In-memory cache for airports to reduce redundant database queries.
    private var cachedAirports: List<Airport>? = null

    // Retrieves airports from cache if available, otherwise fetches from the database.
    // The result is stored in memory for faster access in future requests.
    private suspend fun getOrCacheAirports(): List<Airport> {
        return cachedAirports ?: airportDao.getAllAirports().also {
            cachedAirports = it
        }
    }

    // Retrieves all airports and emits them as a Flow.
    // Uses caching to optimize database access.
    override fun getAllAirports(): Flow<List<Airport>> = flow {
        emit(getOrCacheAirports())
    }

    // Retrieves airports that match a given query and emits them as a Flow.
    // The query is case-insensitive and filters results based on airport name or IATA code.
    override fun getAirportsByQuery(query: String): Flow<List<Airport>> = flow {
        emit(getOrCacheAirports().filter {
            it.name.contains(query, ignoreCase = true) || it.iataCode.contains(query, ignoreCase = true)
        })
    }

    // Checks if a specific route is marked as favorite.
    // Queries the database to verify the existence of the route in the favorites table.
    override suspend fun isFavorite(departureCode: String, destinationCode: String): Boolean {
        return favoriteDao.isFavorite(departureCode, destinationCode)
    }

    // Retrieves all favorite routes as a Flow.
    // The Flow emits updated data when changes occur in the favorites table.
    override fun getAllFavorites(): Flow<List<Favorite>> = favoriteDao.getAllFavorites()

    // Inserts a new favorite route into the database.
    // The operation is performed asynchronously to prevent blocking the main thread.
    override suspend fun insertFavorite(favorite: Favorite) = favoriteDao.insertFavorite(favorite)

    // Removes a favorite route from the database using departure and destination codes.
    // If the favorite does not exist, the operation completes without any changes.
    override suspend fun deleteFavorite(favorite: Favorite) =
        favoriteDao.deleteFavorite(favorite.departureCode, favorite.destinationCode)

    // Saves the user's search query into the DataStore for persistent storage.
    // This allows the app to retain the last searched query even after it is closed.
    override suspend fun saveSearchQuery(query: String) {
        dataStore.edit { preferences ->
            preferences[SEARCH_QUERY_KEY] = query
        }
    }

    // Retrieves the saved search query from the DataStore as a Flow.
    // Returns an empty string if no query has been saved.
    override suspend fun getSavedSearchQuery(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[SEARCH_QUERY_KEY] ?: ""
        }
    }

    companion object {
        // Key used for storing and retrieving the search query from DataStore.
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
    }
}


