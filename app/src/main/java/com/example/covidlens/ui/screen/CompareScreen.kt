package com.example.covidlens.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.covidlens.ui.viewmodel.CompareState
import com.example.covidlens.ui.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CompareScreen(
    vm: MainViewModel,
    navigateBack: () -> Unit
) {
    var countryInput by remember { mutableStateOf("") }
    var selectedCountries by remember { mutableStateOf(emptyList<String>()) }

    val compareState by vm.compareState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp)
        ) {
            // HEADER
            item {
                Text(
                    text = "🌍 Compare Countries",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Add countries to compare their COVID-19 statistics",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(24.dp))
            }

            // INPUT SECTION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Add Country",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = countryInput,
                            onValueChange = { countryInput = it },
                            label = { Text("Country name") },
                            placeholder = { Text("e.g., Mexico, USA, Japan") },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val clean = countryInput.trim()
                                if (clean.isNotEmpty() && !selectedCountries.contains(clean)) {
                                    selectedCountries = selectedCountries + clean
                                }
                                countryInput = ""
                            },
                            enabled = countryInput.isNotBlank(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("➕ Add Country")
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // SELECTED COUNTRIES
            if (selectedCountries.isNotEmpty()) {
                item {
                    Text(
                        text = "Selected Countries (${selectedCountries.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(12.dp))

                    // Chips verticales
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        selectedCountries.forEach { country ->
                            AssistChip(
                                onClick = { selectedCountries = selectedCountries - country },
                                label = { Text(country) },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove",
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                // COMPARE BUTTON
                item {
                    Button(
                        onClick = { vm.compareCountries(selectedCountries) },
                        enabled = compareState !is CompareState.Loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔍 Compare Now", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            // RESULTS
            when (val state = compareState) {
                is CompareState.Error -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "❌ Error",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }

                CompareState.Idle -> {
                    if (selectedCountries.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "📋",
                                        style = MaterialTheme.typography.displayMedium
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Text(
                                        text = "Start adding countries",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = "Add at least one country to compare statistics",
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }
                }

                CompareState.Loading -> {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "Loading comparison...",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }

                is CompareState.Success -> {
                    item {
                        Text(
                            text = "📊 Comparison Results",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    // BAR CHART
                    if (state.data.size > 1) {
                        item {
                            BarComparisonChart(data = state.data)
                            Spacer(Modifier.height(16.dp))
                        }

                        // TIME SERIES CHART
                        item {
                            TimeSeriesChart(data = state.data)
                            Spacer(Modifier.height(24.dp))
                        }
                    }

                    // DETAILED CARDS
                    item {
                        state.data.forEachIndexed { index, entry ->
                            CountryDetailCard(index = index, entry = entry)
                        }
                    }
                }
            }
        }

        // BACK BUTTON
        OutlinedButton(
            onClick = navigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text("← Back to Search")
        }
    }
}

@Composable
private fun BarComparisonChart(data: List<com.example.covidlens.ui.model.CompareResultEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "📊 Total Cases Comparison",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            val maxCases = data.maxOfOrNull { entry ->
                entry.cases.firstOrNull()?.timeline?.lastOrNull()?.total ?: 0
            } ?: 1

            data.forEachIndexed { index, entry ->
                val totalCases = entry.cases.firstOrNull()?.timeline?.lastOrNull()?.total ?: 0
                val percentage = if (maxCases > 0) (totalCases.toFloat() / maxCases) else 0f

                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = entry.country,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = NumberFormat.getNumberInstance(Locale.US).format(totalCases),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        val barColor = when (index % 5) {
                            0 -> MaterialTheme.colorScheme.primary
                            1 -> MaterialTheme.colorScheme.secondary
                            2 -> MaterialTheme.colorScheme.tertiary
                            3 -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth(percentage)
                                .height(12.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(barColor)
                        )
                    }
                }

                if (index < data.size - 1) {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun TimeSeriesChart(data: List<com.example.covidlens.ui.model.CompareResultEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "📈 Cases Over Time",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Evolution of COVID-19 cases",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            // Legend
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                data.forEachIndexed { index, entry ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val lineColor = when (index % 5) {
                            0 -> MaterialTheme.colorScheme.primary
                            1 -> MaterialTheme.colorScheme.secondary
                            2 -> MaterialTheme.colorScheme.tertiary
                            3 -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }

                        Spacer(
                            modifier = Modifier
                                .size(16.dp, 3.dp)
                                .background(lineColor, MaterialTheme.shapes.small)
                        )
                        Spacer(Modifier.padding(4.dp))
                        Text(
                            text = entry.country,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Chart Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val width = size.width
                val height = size.height
                val padding = 40f

                val maxValue = data.maxOfOrNull { entry ->
                    entry.cases.firstOrNull()?.timeline?.maxOfOrNull { it.total } ?: 0
                } ?: 1

                data.forEachIndexed { index, entry ->
                    val timeline = entry.cases.firstOrNull()?.timeline ?: emptyList()
                    if (timeline.isEmpty()) return@forEachIndexed

                    val lineColor = when (index % 5) {
                        0 -> Color(0xFF6200EE)
                        1 -> Color(0xFF03DAC6)
                        2 -> Color(0xFFFF6B35)
                        3 -> Color(0xFFB00020)
                        else -> Color(0xFF9C27B0)
                    }

                    val path = Path()

                    val sampledTimeline = if (timeline.size > 100) {
                        timeline.filterIndexed { i, _ -> i % (timeline.size / 100) == 0 }
                    } else {
                        timeline
                    }

                    sampledTimeline.forEachIndexed { i, point ->
                        val x = padding + (i.toFloat() / sampledTimeline.size) * (width - 2 * padding)
                        val y = height - padding - ((point.total.toFloat() / maxValue) * (height - 2 * padding))

                        if (i == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 3f, cap = StrokeCap.Round)
                    )
                }

                drawLine(
                    color = Color.Gray,
                    start = Offset(padding, height - padding),
                    end = Offset(width - padding, height - padding),
                    strokeWidth = 2f
                )

                drawLine(
                    color = Color.Gray,
                    start = Offset(padding, padding),
                    end = Offset(padding, height - padding),
                    strokeWidth = 2f
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val firstTimeline = data.firstOrNull()?.cases?.firstOrNull()?.timeline
                if (firstTimeline != null && firstTimeline.isNotEmpty()) {
                    Text(
                        text = firstTimeline.first().date.take(7),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = firstTimeline.last().date.take(7),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CountryDetailCard(
    index: Int,
    entry: com.example.covidlens.ui.model.CompareResultEntry
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${index + 1}",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.padding(8.dp))

                Text(
                    text = entry.country,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(16.dp))

            if (entry.error != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "⚠️ ${entry.error}",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else if (entry.cases.isEmpty()) {
                Text(
                    text = "No data available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                val totalRegions = entry.cases.size
                val latestData = entry.cases.firstOrNull()?.timeline?.lastOrNull()
                val totalCases = latestData?.total ?: 0
                val newCases = latestData?.new ?: 0
                val latestDate = latestData?.date ?: "N/A"

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        StatRow("📍 Regions", "$totalRegions")
                        Spacer(Modifier.height(8.dp))
                        StatRow("📅 Latest Date", latestDate)
                        Spacer(Modifier.height(8.dp))
                        StatRow(
                            "🦠 Total Cases",
                            NumberFormat.getNumberInstance(Locale.US).format(totalCases)
                        )
                        Spacer(Modifier.height(8.dp))
                        StatRow(
                            "📈 New Cases",
                            NumberFormat.getNumberInstance(Locale.US).format(newCases),
                            valueColor = if (newCases > 0)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                if (totalRegions > 1) {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Regional Breakdown",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))

                    entry.cases.take(3).forEach { regionCase ->
                        val regionLatest = regionCase.timeline.lastOrNull()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = regionCase.region ?: "National",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = NumberFormat.getNumberInstance(Locale.US)
                                    .format(regionLatest?.total ?: 0),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    if (entry.cases.size > 3) {
                        Text(
                            text = "+ ${entry.cases.size - 3} more region${if (entry.cases.size - 3 != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}