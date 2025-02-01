package com.example.flightsearch.data

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

/*
 * FlightSearchDatabase.kt
 * This file defines the Room database setup for the Flight Search application.
 * It includes entity definitions, DAO interfaces, and the database instance.
 */

@Database(entities = [Airport::class, Favorite::class], version = 1, exportSchema = false)
abstract class FlightSearchDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var instance: FlightSearchDatabase? = null // Ensures a thread-safe singleton instance.

        private const val DATABASE_NAME = "flight_database" // Name of the SQLite database file.
        private const val PRELOADED_DATABASE_PATH = "database/flight_search.db" // Path to a preloaded database asset.

        /**
         * Returns the singleton instance of the database.
         * Uses synchronization to prevent race conditions when multiple threads access it.
         */
        fun getDatabase(context: Context): FlightSearchDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    FlightSearchDatabase::class.java,
                    DATABASE_NAME
                )
                    .createFromAsset(PRELOADED_DATABASE_PATH) // Loads a pre-populated database from assets.
                    .build()
                    .also { instance = it }
            }
        }
    }

    // Provides DAOs to access the database tables.
    abstract fun airportDao(): AirportDao
    abstract fun favoriteDao(): FavoriteDao
}

/*
 * Airport Entity
 * Represents an airport in the database with relevant attributes.
 */
@Entity(tableName = Airport.TABLE_NAME)
data class Airport(
    @PrimaryKey(autoGenerate = true) // Automatically generates a unique ID for each airport.
    val id: Int = 0,

    val name: String, // Name of the airport.

    @ColumnInfo(name = COLUMN_IATA_CODE) // Custom column name for the IATA code.
    val iataCode: String, // International Air Transport Association (IATA) code.

    val passengers: Int // Number of passengers handled by the airport.
) {
    companion object {
        const val TABLE_NAME = "airport"
        const val COLUMN_IATA_CODE = "iata_code"
    }
}

/*
 * Favorite Entity
 * Represents a favorite route saved by the user.
 */
@Entity(tableName = Favorite.TABLE_NAME)
data class Favorite(
    @PrimaryKey(autoGenerate = true) // Automatically generates a unique ID for each favorite.
    val id: Int = 0,

    @ColumnInfo(name = COLUMN_DEPARTURE_CODE) // Departure airport IATA code.
    val departureCode: String,

    @ColumnInfo(name = COLUMN_DESTINATION_CODE) // Destination airport IATA code.
    val destinationCode: String
) {
    companion object {
        const val TABLE_NAME = "favorite"
        const val COLUMN_DEPARTURE_CODE = "departure_code"
        const val COLUMN_DESTINATION_CODE = "destination_code"
    }
}

/*
 * AirportDao (Data Access Object)
 * Defines methods for querying airport data in the database.
 */
@Dao
interface AirportDao {
    companion object {
        private const val SELECT_ALL_QUERY = "SELECT * FROM ${Airport.TABLE_NAME}" // Query for retrieving all airports.
    }

    @Query(SELECT_ALL_QUERY)
    suspend fun getAllAirports(): List<Airport> // Retrieves a list of all airports.
}

/*
 * FavoriteDao (Data Access Object)
 * Defines methods for managing favorite routes in the database.
 */
@Dao
interface FavoriteDao {
    companion object {
        private const val SELECT_ALL_FAVORITES_QUERY = "SELECT * FROM ${Favorite.TABLE_NAME} ORDER BY ${Favorite.COLUMN_DEPARTURE_CODE} DESC"
        private const val DELETE_FAVORITE_QUERY = "DELETE FROM ${Favorite.TABLE_NAME} WHERE ${Favorite.COLUMN_DEPARTURE_CODE} = :departureCode AND ${Favorite.COLUMN_DESTINATION_CODE} = :destinationCode"
        private const val IS_FAVORITE_QUERY = "SELECT COUNT(*) > 0 FROM ${Favorite.TABLE_NAME} WHERE ${Favorite.COLUMN_DEPARTURE_CODE} = :departureCode AND ${Favorite.COLUMN_DESTINATION_CODE} = :destinationCode"
    }

    @Query(SELECT_ALL_FAVORITES_QUERY)
    fun getAllFavorites(): Flow<List<Favorite>> // Retrieves a list of favorite routes, sorted by departure code.

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite) // Inserts a new favorite or updates an existing one.

    @Query(DELETE_FAVORITE_QUERY)
    suspend fun deleteFavorite(departureCode: String, destinationCode: String) // Removes a favorite route.

    @Query(IS_FAVORITE_QUERY)
    suspend fun isFavorite(departureCode: String, destinationCode: String): Boolean // Checks if a route is marked as a favorite.
}






