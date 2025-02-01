package com.example.flightsearch.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.ui.Screen
import com.example.flightsearch.ui.components.SearchTextField


/*
 * HomeScreen.kt
 * This composable function defines the UI and navigation behavior for the HomeScreen.
 * It manages user input (search query) and triggers navigation events.
 */

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = viewModel(factory = HomeScreenViewModel.Factory),
    navigateControl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val query by viewModel.query.collectAsState()

    // Trigger navigation when the query state changes
    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            navigateControl("${Screen.AirportsScreen.route}/$query")
        } else {
            navigateControl(Screen.FavoritesScreen.route)
        }
    }

    Column(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchTextField(
            value = query,
            onValueChange = { newQuery -> viewModel.saveSearchQuery(newQuery) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.padding_small))
                .defaultMinSize(minHeight = dimensionResource(id = R.dimen.text_field_height))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchTextField(
            value = stringResource(id = R.string.search_airports_placeholder),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.padding_small))
                .defaultMinSize(minHeight = dimensionResource(id = R.dimen.text_field_height))
        )
    }
}















