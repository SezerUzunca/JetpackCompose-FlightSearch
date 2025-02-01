package com.example.flightsearch.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.flightsearch.R
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Favorite

/*
 * UI Components for Flight Search App
 * This file contains composable functions for displaying airport details, search input,
 * flight route items, error and loading states, and previews.
 */

@Composable
// Displays a flight route card with departure and destination airports, including a favorite button.
fun FlightRouteItem(
    departure: Airport,
    destination: Airport,
    isFavorite: Boolean,
    onToggleFavorite: (Favorite) -> Unit,
    modifier: Modifier = Modifier
) {
    val favorite = Favorite(
        departureCode = departure.iataCode,
        destinationCode = destination.iataCode
    )

    Card(
        shape = RoundedCornerShape(topEnd = dimensionResource(R.dimen.card_corner_radius)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.padding_small)),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                AirportDetails(
                    title = stringResource(id = R.string.label_departure),
                    iataCode = departure.iataCode,
                    name = departure.name
                )
                AirportDetails(
                    title = stringResource(id = R.string.label_destination),
                    iataCode = destination.iataCode,
                    name = destination.name
                )
            }

            IconButton(
                onClick = { onToggleFavorite(favorite) },
                modifier = Modifier
                    .size(dimensionResource(R.dimen.icon_size_small))
                    .padding(end = dimensionResource(R.dimen.padding_small))
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = if (isFavorite)
                        stringResource(id = R.string.remove_from_favorites)
                    else
                        stringResource(id = R.string.add_to_favorites),
                    tint = if (isFavorite) Color.Yellow else LocalContentColor.current
                )
            }
        }
    }
}

@Composable
// Displays detailed information about an airport, including its IATA code and name.
fun AirportDetails(
    iataCode: String,
    name: String,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    Column(modifier = modifier) {
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleSmall
            )
        }

        Text(
            buildAnnotatedString {
                withStyle(
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        .toSpanStyle()
                ) {
                    append(iataCode)
                }
                append(" ")
                withStyle(style = MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                    append(name)
                }
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
// Displays a list of airports in a scrollable LazyColumn.
fun AirportsList(
    airports: List<Airport>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(airports) { airport ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small))
                    .clickable { onItemClick(airport.id) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                AirportDetails(
                    iataCode = airport.iataCode,
                    name = airport.name,
                    title = null
                )
            }
        }
    }
}

@Composable
// A search input field with leading and trailing icons.
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(stringResource(id = R.string.search_airports_placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.icon_search),
                tint = Color.Gray
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = stringResource(id = R.string.icon_mic),
                tint = Color.Gray
            )
        },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
        singleLine = true,
        modifier = modifier
    )
}

@Composable
// Displays an error message in red color.
fun ErrorState(
    errorMessage: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        text = stringResource(id = R.string.error_occurred, errorMessage),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = textAlign,
        modifier = modifier
    )
}

@Composable
// Displays a loading indicator in the center of the screen.
fun LoadingState() {
    Box(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_large))
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        CircularProgressIndicator()
    }
}

@Composable
// Displays a message when the screen is in an idle state.
fun IdleState(
    message: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        text = message,
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        style = textStyle,
        textAlign = textAlign
    )
}

// Previews

@Preview(showBackground = true)
@Composable
// Preview for FlightRouteItem composable.
fun FlightRouteItemPreview() {
    FlightRouteItem(
        departure = Airport(1, stringResource(R.string.mock_airport_lax), "LAX", 10000000),
        destination = Airport(2, stringResource(R.string.mock_airport_jfk), "JFK", 8000000),
        isFavorite = true,
        onToggleFavorite = {}
    )
}

@Preview(showBackground = true)
@Composable
// Preview for AirportDetails composable.
fun AirportDetailsPreview() {
    AirportDetails(
        title = stringResource(id = R.string.label_departure),
        iataCode = "LAX",
        name = "Los Angeles International Airport"
    )
}

@Preview(showBackground = true)
@Composable
// Preview for ErrorState composable.
fun ErrorStatePreview() {
    ErrorState(
        errorMessage = "Network issue",
    )
}

@Preview(showBackground = true)
@Composable
// Preview for LoadingState composable.
fun LoadingStatePreview() {
    LoadingState()
}

@Preview(showBackground = true)
@Composable
// Preview for AirportsList composable.
fun AirportsListPreview() {
    val mockAirports = listOf(
        Airport(1, stringResource(id = R.string.mock_airport_lax), "LAX", 10000000),
        Airport(2, stringResource(id = R.string.mock_airport_jfk), "JFK", 8000000),
        Airport(3, stringResource(id = R.string.mock_airport_sfo), "SFO", 9000000)
    )

    AirportsList(
        airports = mockAirports,
        onItemClick = { id -> println("Clicked on airport with ID: $id") }
    )
}

@Preview(showBackground = true)
@Composable
// Preview for IdleState composable.
fun IdleStatePreview() {
    IdleState(
        message = "This is an idle state message",
        textStyle = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
// Preview for SearchTextField composable.
fun SearchTextFieldPreview() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SearchTextField(
            value = stringResource(id = R.string.sample_query),
            onValueChange = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}



