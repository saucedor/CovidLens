package com.example.covidlens.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.covidlens.ui.model.CompareResultEntry
import com.example.covidlens.ui.model.TimelineItem

@Composable
fun CompareResultsList(results: List<CompareResultEntry>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(results) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp)) {
                    // Country name
                    Text(entry.country, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))

                    if (entry.error != null) {
                        // If API failed for this country
                        Text(
                            text = "Error loading data: ${entry.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (entry.cases.isEmpty()) {
                        // If the API returned no regions
                        Text("No data available.")
                    } else {
                        // Pick the region with the biggest timeline (more complete)
                        val mainRegion = entry.cases.maxByOrNull { it.timeline.size }

                        if (mainRegion != null && mainRegion.timeline.isNotEmpty()) {
                            // Convert model → chart data
                            val chartData = mainRegion.timeline.map {
                                TimelineItem(date = it.date, total = it.total, new = it.new)
                            }

                            TrendChart(
                                data = chartData,
                                modifier = Modifier.fillMaxWidth().height(140.dp)
                            )
                            Spacer(Modifier.height(8.dp))

                            // Latest number
                            mainRegion.timeline.lastOrNull()?.let {
                                Text("Latest: ${it.date} — total ${it.total}, new ${it.new}")
                            }
                        } else {
                            Text("No timeline data available.")
                        }
                    }
                }
            }
        }
    }
}
