package com.example.flightsearch.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.repository.FlightSearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/*
 * HomeScreenViewModel.kt
 * ViewModel for managing the state and data of the HomeScreen.
 * It handles search queries, saves and retrieves them from DataStore.
 */

class HomeScreenViewModel(private val flightSearchRepository: FlightSearchRepository) :
    ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    init {
        viewModelScope.launch {
            try {
                flightSearchRepository.getSavedSearchQuery()
                    .collect { savedQuery ->
                        _query.value = savedQuery
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading query: ${e.message}")
            }
        }
    }

    fun saveSearchQuery(newQuery: String) {
        _query.value = newQuery
        viewModelScope.launch {
            try {
                flightSearchRepository.saveSearchQuery(newQuery)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving query: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "HomeScreenViewModel"

        // Factory for ViewModel dependency injection
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as FlightSearchApplication
                HomeScreenViewModel(flightSearchRepository = application.container.flightSearchRepository)
            }
        }
    }
}





