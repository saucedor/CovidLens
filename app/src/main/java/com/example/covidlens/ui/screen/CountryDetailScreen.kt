package com.example.covidlens.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.covidlens.domain.model.RegionCases
import com.example.covidlens.ui.components.DateSelector
import com.example.covidlens.ui.viewmodel.MainViewModel
import com.example.covidlens.ui.viewmodel.UiState
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    country: String,
    vm: MainViewModel,
    navigateBack: () -> Unit
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(country) {
        vm.searchCountry(country)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Country Details") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        when (val currentState = state) {
            UiState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Select a country to view details")
                }
            }

            UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Loading data for $country...")
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(20.dp)
                ) {
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
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = currentState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { vm.searchCountry(country) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }

            is UiState.SuccessCountry -> {
                CountryDetailContent(
                    country = country,
                    regions = currentState.data,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun CountryDetailContent(
    country: String,
    regions: List<RegionCases>,
    modifier: Modifier = Modifier
) {
    var selectedRegionIndex by remember { mutableIntStateOf(0) }
    val selectedRegion = regions.getOrNull(selectedRegionIndex) ?: return

    var selectedDateIndex by remember(selectedRegion) {
        mutableIntStateOf(selectedRegion.timeline.lastIndex.coerceAtLeast(0))
    }

    val timeline = selectedRegion.timeline
    val selectedData = timeline.getOrNull(selectedDateIndex)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // HEADER
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🌍",
                    style = MaterialTheme.typography.displaySmall
                )
                Spacer(Modifier.padding(8.dp))
                Column {
                    Text(
                        text = country,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (selectedRegion.region != null) {
                        Text(
                            text = "Region: ${selectedRegion.region}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // REGION SELECTOR (if multiple regions)
        if (regions.size > 1) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📍 Select Region",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))
                        DateSelector(
                            dates = regions.map { it.region ?: "National" },
                            selected = selectedRegion.region ?: "National",
                            onSelect = { selected ->
                                selectedRegionIndex = regions.indexOfFirst {
                                    (it.region ?: "National") == selected
                                }.coerceAtLeast(0)
                            }
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }

        // DATE SELECTOR
        if (timeline.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📅 Select Date",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))
                        DateSelector(
                            dates = timeline.map { it.date },
                            selected = selectedData?.date ?: "",
                            onSelect = { selected ->
                                selectedDateIndex = timeline.indexOfFirst { it.date == selected }
                                    .coerceAtLeast(0)
                            }
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // SELECTED DATE STATS
            if (selectedData != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "📊 Statistics",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(16.dp))

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    StatRow("📅 Date", selectedData.date)
                                    Spacer(Modifier.height(12.dp))
                                    StatRow(
                                        "🦠 Total Cases",
                                        NumberFormat.getNumberInstance(Locale.US)
                                            .format(selectedData.total)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    StatRow(
                                        "📈 New Cases",
                                        NumberFormat.getNumberInstance(Locale.US)
                                            .format(selectedData.new),
                                        valueColor = if (selectedData.new > 0)
                                            MaterialTheme.colorScheme.error
                                        else
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            // Navigation buttons
                            Spacer(Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        if (selectedDateIndex > 0) {
                                            selectedDateIndex--
                                        }
                                    },
                                    enabled = selectedDateIndex > 0
                                ) {
                                    Text("← Previous")
                                }

                                Text(
                                    text = "${selectedDateIndex + 1} / ${timeline.size}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )

                                OutlinedButton(
                                    onClick = {
                                        if (selectedDateIndex < timeline.lastIndex) {
                                            selectedDateIndex++
                                        }
                                    },
                                    enabled = selectedDateIndex < timeline.lastIndex
                                ) {
                                    Text("Next →")
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }

            // TIME SERIES CHART
            item {
                TimeSeriesLineChart(timeline = timeline, currentIndex = selectedDateIndex)
                Spacer(Modifier.height(20.dp))
            }

            // COMPLETE TIMELINE HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 Complete Timeline",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${timeline.size} entries",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            // TIMELINE CARDS (reversed to show newest first)
            items(timeline.size) { index ->
                val reverseIndex = timeline.lastIndex - index
                val entry = timeline[reverseIndex]
                TimelineCard(
                    caseCount = entry,
                    isSelected = reverseIndex == selectedDateIndex
                )
                Spacer(Modifier.height(8.dp))
            }
        } else {
            item {
                Text(
                    text = "No timeline data available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun TimeSeriesLineChart(
    timeline: List<com.example.covidlens.domain.model.CaseCount>,
    currentIndex: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "📈 Cases Over Time",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val width = size.width
                val height = size.height
                val padding = 40f

                val maxValue = timeline.maxOfOrNull { it.total } ?: 1

                // Draw line
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
                    color = Color(0xFF6200EE),
                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                )

                // Draw current point indicator
                if (currentIndex in timeline.indices) {
                    val x = padding + (currentIndex.toFloat() / timeline.size) * (width - 2 * padding)
                    val y = height - padding - ((timeline[currentIndex].total.toFloat() / maxValue) * (height - 2 * padding))

                    drawCircle(
                        color = Color(0xFFFF6B35),
                        radius = 8f,
                        center = Offset(x, y)
                    )
                }

                // Draw axes
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
                Text(
                    text = timeline.first().date.take(7),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "● Current",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFF6B35),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = timeline.last().date.take(7),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TimelineCard(
    caseCount: com.example.covidlens.domain.model.CaseCount,
    isSelected: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isSelected) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) else CardDefaults.cardColors(),
        elevation = if (isSelected) CardDefaults.cardElevation(defaultElevation = 4.dp)
        else CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = caseCount.date,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "New: ${NumberFormat.getNumberInstance(Locale.US).format(caseCount.new)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (caseCount.new > 0) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = NumberFormat.getNumberInstance(Locale.US).format(caseCount.total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Spacer(Modifier.padding(8.dp))
                Text(
                    text = "●",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
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
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}