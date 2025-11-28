package com.example.covidlens.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.covidlens.ui.components.StatCard
import com.example.covidlens.ui.components.TrendChart
import com.example.covidlens.ui.state.UiState
import com.example.covidlens.ui.util.averageGrowth
import com.example.covidlens.ui.util.toReadableDate
import com.example.covidlens.ui.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CompareScreen(vm: MainViewModel) {
    val state by vm.state.collectAsState()
    val inputs = remember { mutableStateListOf("", "") }
    var infoMessage by remember { mutableStateOf<String?>(null) }
    val numberFormatter = remember { NumberFormat.getIntegerInstance(Locale.getDefault()) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Compare countries", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Add at least two countries and review their evolution side by side.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "Use English country names (e.g. United States) so the API can resolve them.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        inputs.forEachIndexed { index, value ->
            OutlinedTextField(
                value = value,
                onValueChange = { newValue -> inputs[index] = newValue },
                label = { Text("Country ${index + 1}") },
                placeholder = { Text("e.g. Canada") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                trailingIcon = if (inputs.size > 2) {
                    {
                        TextButton(onClick = { inputs.removeAt(index) }) {
                            Text("Remove")
                        }
                    }
                } else null
            )
        }

        if (inputs.size < 4) {
            TextButton(onClick = { inputs.add("") }) {
                Text("Add another country")
            }
        }

        infoMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        val trimmedInputs = inputs.map { it.trim() }.filter { it.isNotBlank() }
        val isLoading = state == UiState.Loading

        Button(
            onClick = {
                infoMessage = null
                if (trimmedInputs.size < 2) {
                    infoMessage = "Please enter at least two countries."
                } else {
                    vm.compareCountries(trimmedInputs)
                }
            },
            enabled = trimmedInputs.isNotEmpty() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .height(18.dp),
                    strokeWidth = 2.dp
                )
            }
            Text(if (isLoading) "Comparing…" else "Compare")
        }

        when (val s = state) {
            is UiState.Error -> CompareError(message = s.message, onRetry = { vm.retry() })
            is UiState.SuccessComparison -> ComparisonResults(s.data, numberFormatter)
            else -> if (isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                for (i in inputs.indices) {
                    inputs[i] = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Clear inputs")
        }
    }
}

@Composable
private fun CompareError(message: String, onRetry: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Comparison failed", style = MaterialTheme.typography.titleMedium)
            Text(message, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onRetry) {
                Text("Try again")
            }
        }
    }
}

@Composable
private fun ComparisonResults(
    countries: List<CountryStats>,
    formatter: NumberFormat
) {
    if (countries.isEmpty()) {
        Text("No data was returned for those countries.")
        return
    }

    val ordered = remember(countries) {
        countries.sortedByDescending { it.timeline.lastOrNull()?.confirmed ?: 0 }
    }

    ordered.firstOrNull()?.let { LeaderHighlight(it, formatter) }

    ordered.forEach { stats ->
        ComparisonCard(stats, formatter)
    }
}

@Composable
private fun LeaderHighlight(leader: CountryStats, formatter: NumberFormat) {
    val total = leader.timeline.lastOrNull()?.confirmed ?: 0
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Highest total", style = MaterialTheme.typography.labelMedium)
            Text(leader.country, style = MaterialTheme.typography.titleLarge)
            Text("${formatter.format(total)} confirmed cases", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ComparisonCard(stats: CountryStats, formatter: NumberFormat) {
    val timeline = stats.timeline
    if (timeline.isEmpty()) return

    val latest = timeline.last()
    val peakDay = timeline.maxByOrNull { it.confirmed ?: 0 }
    val average = timeline.averageGrowth()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stats.country, style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                StatCard("Total cases", latest.confirmed ?: 0)
                StatCard("Avg. daily", average)
                StatCard("Recovered", latest.recovered)
                StatCard("Deaths", latest.deaths)
            }
            peakDay?.let {
                Text(
                    "Peak: ${formatter.format(it.confirmed ?: 0)} cases on ${it.date.toReadableDate()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            TrendChart(values = timeline.map { it.confirmed ?: 0 })
        }
    }
}
