package com.example.flightsearch.ui.screens.airports

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.ui.components.AirportsList
import com.example.flightsearch.ui.components.ErrorState
import com.example.flightsearch.ui.components.LoadingState


/*
 * AirportsScreen.kt
 * Composable function for displaying airport search results.
 * It observes the ViewModel state and updates the UI accordingly.
 */

@Composable
fun AirportsScreen(
    viewModel: AirportsScreenViewModel = viewModel(factory = AirportsScreenViewModel.Factory),
    query: String,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Reload airport data whenever the search query changes
    LaunchedEffect(query) {
        viewModel.loadData(query)
    }

    when (uiState) {
        is AirportsScreenUiState.Error -> {
            val errorMessage = (uiState as AirportsScreenUiState.Error).message
            ErrorState(
                errorMessage = errorMessage,
                modifier = modifier
            )
        }

        AirportsScreenUiState.Loading -> {
            LoadingState()
        }

        is AirportsScreenUiState.Success -> {
            val airports = (uiState as AirportsScreenUiState.Success).airports
            AirportsList(
                airports = airports,
                onItemClick = onItemClick,
                modifier = modifier
            )
        }
    }
}






