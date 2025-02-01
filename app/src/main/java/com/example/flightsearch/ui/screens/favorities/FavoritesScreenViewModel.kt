package com.example.flightsearch.ui.screens.favorities

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.repository.FlightSearchRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/*
 * FavoritesScreenViewModel.kt
 * ViewModel for managing the state and data of the FavoritesScreen.
 * It loads favorite routes, handles data updates, and manages UI state.
 */

class FavoritesScreenViewModel(private val flightSearchRepository: FlightSearchRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesScreenUiState>(FavoritesScreenUiState.Loading)
    val uiState: StateFlow<FavoritesScreenUiState> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                delay(TIMEOUT_DURATION)

                combine(
                    flightSearchRepository.getAllAirports(),
                    flightSearchRepository.getAllFavorites()
                ) { airports, favorites ->
                    if (favorites.isEmpty()) {
                        FavoritesScreenUiState.Idle
                    } else {
                        FavoritesScreenUiState.Success(favorites = favorites, airports = airports)
                    }
                }.collect { uiState ->
                    _uiState.value = uiState
                }
            } catch (e: Exception) {
                _uiState.value =
                    FavoritesScreenUiState.Error(message = e.message ?: UNKNOWN_ERROR_MESSAGE)
                Log.e(TAG, ERROR_LOADING_DATA_MESSAGE, e)
            }
        }
    }

    fun toggleFavorite(favorite: Favorite) {
        viewModelScope.launch {
            try {
                val isCurrentlyFavorite = flightSearchRepository.isFavorite(
                    departureCode = favorite.departureCode,
                    destinationCode = favorite.destinationCode
                )

                if (isCurrentlyFavorite) {
                    flightSearchRepository.deleteFavorite(favorite)
                } else {
                    flightSearchRepository.insertFavorite(favorite)
                }
            } catch (e: Exception) {
                Log.e(TAG, ERROR_TOGGLING_FAVORITE_MESSAGE, e)
                _uiState.value = FavoritesScreenUiState.Error(
                    message = ERROR_TOGGLING_FAVORITE_MESSAGE.format(
                        e.localizedMessage ?: UNKNOWN_ERROR_MESSAGE
                    )
                )
            }
        }
    }

    companion object {
        private const val TAG = "FavoritesScreenViewModel"
        private const val TIMEOUT_DURATION = 500L

        private const val UNKNOWN_ERROR_MESSAGE = "Unknown error"
        private const val ERROR_LOADING_DATA_MESSAGE = "Error loading data"
        private const val ERROR_TOGGLING_FAVORITE_MESSAGE =
            "An error occurred while toggling favorite: %s"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                FavoritesScreenViewModel(flightSearchRepository = application.container.flightSearchRepository)
            }
        }
    }
}

// Represents different UI states for the FavoritesScreen
sealed class FavoritesScreenUiState {
    data class Success(
        val favorites: List<Favorite>,
        val airports: List<Airport>
    ) : FavoritesScreenUiState()

    data class Error(val message: String) : FavoritesScreenUiState()

    data object Idle : FavoritesScreenUiState()
    data object Loading : FavoritesScreenUiState()
}


