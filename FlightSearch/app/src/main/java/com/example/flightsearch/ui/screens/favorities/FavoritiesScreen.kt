package com.example.flightsearch.ui.screens.favorities

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.ui.components.ErrorState
import com.example.flightsearch.ui.components.FlightRouteItem
import com.example.flightsearch.ui.components.IdleState
import com.example.flightsearch.ui.components.LoadingState

/*
 * FavoritesScreen.kt
 * This composable function displays the user's favorite flight routes.
 * It manages UI states such as loading, idle (no favorites), error, and success.
 */

@SuppressLint("SuspiciousIndentation") // Suppresses indentation warning if necessary
@Composable
fun FavoritesScreen(
    viewModel: FavoritesScreenViewModel = viewModel(factory = FavoritesScreenViewModel.Factory),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        FavoritesScreenUiState.Loading -> {
            // Show loading state while data is being fetched
            LoadingState()
        }

        FavoritesScreenUiState.Idle -> {
            // Display message when there are no favorite routes
            IdleState(
                message = stringResource(id = R.string.no_favorites),
                modifier = modifier,
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }

        is FavoritesScreenUiState.Error -> {
            val errorMessage = (uiState as FavoritesScreenUiState.Error).message
            ErrorState(
                errorMessage = errorMessage,
                modifier = modifier,
            )
        }

        is FavoritesScreenUiState.Success -> {
            val favorites = (uiState as FavoritesScreenUiState.Success).favorites
            val airports = (uiState as FavoritesScreenUiState.Success).airports

            // Convert airports list into a map for quick lookup
            val airportsMap = airports.associateBy { it.iataCode }

            LazyColumn(modifier = modifier) {
                items(favorites) { favorite ->
                    val departureAirport = airportsMap[favorite.departureCode]
                        ?: Airport(
                            id = 0,
                            iataCode = favorite.departureCode,
                            name = stringResource(id = R.string.data_not_found),
                            passengers = 0
                        )

                    val destinationAirport = airportsMap[favorite.destinationCode]
                        ?: Airport(
                            id = 0,
                            iataCode = favorite.destinationCode,
                            name = stringResource(id = R.string.data_not_found),
                            passengers = 0
                        )

                    FlightRouteItem(
                        departure = departureAirport,
                        destination = destinationAirport,
                        isFavorite = true,
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.padding_small))
                    )
                }
            }
        }
    }
}


















