package com.example.flightsearch.ui.screens.airports

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.Airport
import com.example.flightsearch.repository.FlightSearchRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/*
 * AirportsScreenViewModel.kt
 * ViewModel for managing the state and data of the AirportsScreen.
 * It handles loading airport data based on a search query and updates the UI state.
 */

class AirportsScreenViewModel(private val flightSearchRepository: FlightSearchRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<AirportsScreenUiState>(AirportsScreenUiState.Loading)
    val uiState: StateFlow<AirportsScreenUiState> = _uiState

    fun loadData(query: String) {
        viewModelScope.launch {
            _uiState.value = AirportsScreenUiState.Loading

            try {
                delay(TIMEOUT_DURATION)

                flightSearchRepository.getAirportsByQuery(query).collect { airports ->
                    _uiState.value = if (airports.isNotEmpty()) {
                        AirportsScreenUiState.Success(airports)
                    } else {
                        AirportsScreenUiState.Error(NO_AIRPORTS_FOUND_MESSAGE.format(query))
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AirportsScreenUiState.Error(
                    e.localizedMessage ?: UNKNOWN_ERROR_MESSAGE
                )
                Log.e(TAG, ERROR_LOADING_AIRPORTS_MESSAGE, e)
            }
        }
    }

    companion object {
        private const val TAG = "AirportsScreenViewModel"
        private const val TIMEOUT_DURATION = 500L

        private const val NO_AIRPORTS_FOUND_MESSAGE = "No airports found for query: %s"
        private const val UNKNOWN_ERROR_MESSAGE = "Unknown error occurred"
        private const val ERROR_LOADING_AIRPORTS_MESSAGE = "Error loading airports"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                AirportsScreenViewModel(flightSearchRepository = application.container.flightSearchRepository)
            }
        }
    }
}

// UI states for AirportsScreen
sealed class AirportsScreenUiState {
    data class Success(val airports: List<Airport>) : AirportsScreenUiState()
    data class Error(val message: String) : AirportsScreenUiState()
    data object Loading : AirportsScreenUiState()
}
