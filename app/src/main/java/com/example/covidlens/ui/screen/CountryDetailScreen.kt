package com.example.covidlens.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.covidlens.ui.components.StatCard
import com.example.covidlens.ui.components.TrendChart
import com.example.covidlens.ui.state.UiState
import com.example.covidlens.ui.viewmodel.MainViewModel

@Composable
fun CountryDetailScreen(vm: MainViewModel) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
            .verticalScroll(rememberScrollState())
    ) {
        when (val s = state) {
            is UiState.SuccessCountry -> {
                val stats = s.data
                val lastDay = stats.timeline.lastOrNull()

                // --- Header ---
                Text(stats.country, style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(24.dp))

                // --- Stat Cards ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(label = "Confirmed", value = lastDay?.confirmed)
                    StatCard(label = "Deaths", value = lastDay?.deaths)
                    StatCard(label = "Recovered", value = lastDay?.recovered)
                }

                Spacer(Modifier.height(40.dp))

                // --- Trend Chart ---
                Text("Cases Trend", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                val chartValues = stats.timeline.mapNotNull { it.confirmed }
                TrendChart(values = chartValues)

            }
            is UiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Oops! Something went wrong", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(s.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { vm.retry() }) {
                        Text("Try again")
                    }
                }
            }
            UiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Loading country data...")
                }
            }
            else -> {
                Text("Search for a country to see its stats.")
            }
        }
    }
}
