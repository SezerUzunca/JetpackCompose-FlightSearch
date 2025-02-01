package com.example.flightsearch.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flightsearch.R
import com.example.flightsearch.ui.screens.airports.AirportsScreen
import com.example.flightsearch.ui.screens.favorities.FavoritesScreen
import com.example.flightsearch.ui.screens.home.HomeScreen
import com.example.flightsearch.ui.screens.routes.RoutesScreen

/*
 * FlightSearchApp.kt
 * This file defines the main navigation and UI structure of the Flight Search application.
 * It includes a sealed class for defining screen routes and a Composable function
 * to manage navigation using Jetpack Compose.
 */

sealed class Screen(val route: String) {
    data object FavoritesScreen : Screen("favorites_screen") // Favorites screen route
    data object AirportsScreen : Screen("airports_screen")   // Airports screen route
    data object RoutesScreen : Screen("routes_screen")       // Routes screen route
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchApp() {
    val navController = rememberNavController() // Navigation controller for app navigation

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }, // Displays app title
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Adjust layout padding
        ) {
            HomeScreen(
                navigateControl = { query ->
                    navController.navigate(query) // Handle navigation requests
                },
                modifier = Modifier.fillMaxSize()
            )

            // Navigation host defining app screens and their routes
            NavHost(
                navController = navController,
                startDestination = Screen.FavoritesScreen.route, // Default start screen
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Favorites Screen
                composable(route = Screen.FavoritesScreen.route) {
                    FavoritesScreen(modifier = Modifier.fillMaxSize())
                }

                // Airports Screen with a dynamic query argument
                composable(
                    route = "${Screen.AirportsScreen.route}/{query}",
                    arguments = listOf(navArgument("query") { type = NavType.StringType })
                ) { backStackEntry ->
                    val query = backStackEntry.arguments?.getString("query") ?: ""
                    AirportsScreen(
                        query = query,
                        modifier = Modifier.fillMaxSize(),
                        onItemClick = { airportId ->
                            navController.navigate("${Screen.RoutesScreen.route}/$airportId")
                        }
                    )
                }

                // Routes Screen with a dynamic airportId argument
                composable(
                    route = "${Screen.RoutesScreen.route}/{airportId}",
                    arguments = listOf(navArgument("airportId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val airportId = backStackEntry.arguments?.getInt("airportId")
                    airportId?.let {
                        RoutesScreen(
                            airportId = it,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}






