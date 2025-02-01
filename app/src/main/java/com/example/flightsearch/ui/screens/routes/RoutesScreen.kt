package com.example.flightsearch.ui.screens.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.ui.components.ErrorState
import com.example.flightsearch.ui.components.FlightRouteItem
import com.example.flightsearch.ui.components.IdleState
import com.example.flightsearch.ui.components.LoadingState

/*
 * RoutesScreen.kt
 * This composable function displays a list of available flight routes
 * from a selected airport. It manages UI states such as loading, error,
 * and success using a ViewModel.
 */

@Composable
fun RoutesScreen(
    viewModel: RoutesScreenViewModel = viewModel(factory = RoutesScreenViewModel.Factory),
    airportId: Int,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is RoutesScreenUiState.Loading -> {
            // Show loading indicator while fetching data
            LoadingState()
        }

        is RoutesScreenUiState.Error -> {
            val errorMessage = (uiState as RoutesScreenUiState.Error).message
            ErrorState(
                errorMessage = errorMessage,
                modifier = modifier,
            )
        }

        is RoutesScreenUiState.Success -> {
            val airports = (uiState as RoutesScreenUiState.Success).airports
            val favorites = (uiState as RoutesScreenUiState.Success).favorites

            val (currentAirport, filteredAirports) = remember(airportId, airports) {
                val current = airports.find { it.id == airportId }
                val filtered = airports.filter { it.id != airportId }
                current to filtered
            }

            val favoriteRoutesSet = remember(favorites) {
                favorites.map { it.departureCode to it.destinationCode }.toSet()
            }

            Column(modifier = modifier) {
                currentAirport?.let { currentAirport ->
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(filteredAirports) { airport ->
                            val isFavorite = remember(currentAirport, favoriteRoutesSet) {
                                (currentAirport.iataCode to airport.iataCode) in favoriteRoutesSet
                            }

                            FlightRouteItem(
                                departure = currentAirport,
                                destination = airport,
                                isFavorite = isFavorite,
                                onToggleFavorite = { favorite ->
                                    viewModel.toggleFavorite(favorite)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(id = R.dimen.padding_small))
                            )
                        }
                    }
                } ?: run {
                    // Show message when no valid airport is selected
                    IdleState(
                        message = stringResource(id = R.string.no_current_airport),
                        modifier = modifier,
                        textStyle = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

















