package com.example.flightsearch.ui.screens.routes

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
 * RoutesScreenViewModel.kt
 * ViewModel for managing the state and data of the RoutesScreen.
 * It handles loading airport data, managing favorite routes, and updating UI state.
 */

class RoutesScreenViewModel(private val flightSearchRepository: FlightSearchRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<RoutesScreenUiState>(RoutesScreenUiState.Loading)
    val uiState: StateFlow<RoutesScreenUiState> = _uiState

    init {
        loadData()
    }

    // Loads airport and favorite route data
    private fun loadData() {
        viewModelScope.launch {
            try {
                delay(TIMEOUT_DURATION)

                combine(
                    flightSearchRepository.getAllAirports(),
                    flightSearchRepository.getAllFavorites()
                ) { airports, favorites ->
                    RoutesScreenUiState.Success(airports = airports, favorites = favorites)
                }.collect { uiState ->
                    _uiState.value = uiState
                }
            } catch (e: Exception) {
                _uiState.value = RoutesScreenUiState.Error(
                    message = e.message ?: UNKNOWN_ERROR_MESSAGE
                )
                Log.e(TAG, ERROR_LOADING_DATA_MESSAGE, e)
            }
        }
    }

    // Toggles a route between favorite and non-favorite
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
                _uiState.value = RoutesScreenUiState.Error(
                    message = ERROR_TOGGLING_FAVORITE_MESSAGE.format(
                        e.localizedMessage ?: UNKNOWN_ERROR_MESSAGE
                    )
                )
            }
        }
    }

    companion object {
        private const val TAG = "RoutesScreenViewModel"
        private const val TIMEOUT_DURATION = 500L

        private const val UNKNOWN_ERROR_MESSAGE = "Unknown error"
        private const val ERROR_LOADING_DATA_MESSAGE = "Error loading data"
        private const val ERROR_TOGGLING_FAVORITE_MESSAGE =
            "An error occurred while toggling favorite: %s"

        // Factory for ViewModel dependency injection
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                RoutesScreenViewModel(flightSearchRepository = application.container.flightSearchRepository)
            }
        }
    }
}

// UI state representation for RoutesScreen
sealed class RoutesScreenUiState {
    data class Success(
        val airports: List<Airport>,
        val favorites: List<Favorite>
    ) : RoutesScreenUiState()

    data class Error(val message: String) : RoutesScreenUiState()

    data object Loading : RoutesScreenUiState()
}

