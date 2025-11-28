package com.example.covidlens.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.covidlens.domain.model.RegionCases
import com.example.covidlens.ui.viewmodel.MainViewModel
import com.example.covidlens.ui.viewmodel.UiState
import com.example.covidlens.ui.viewmodel.UiStateGlobal
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    vm: MainViewModel,
    onCountryClick: (String) -> Unit,
    onCompareClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var countryInput by remember { mutableStateOf("") }

    val searchState by vm.state.collectAsState()
    val globalState by vm.globalState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Load last searched country
    LaunchedEffect(Unit) {
        vm.loadLastCountry { lastCountry ->
            lastCountry?.let {
                countryInput = it
                vm.searchCountry(it)
            }
        }
    }

    // Load global snapshot when switching to Global tab
    LaunchedEffect(selectedTab) {
        if (selectedTab == 1 && globalState is UiStateGlobal.Idle) {
            vm.loadGlobalSnapshot()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("COVID-19 Lens") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCompareClick) {
                Icon(Icons.Filled.Public, contentDescription = "Compare Countries")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // TABS
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("🔍 Search") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("🌍 Global") }
                )
            }

            // TAB CONTENT
            when (selectedTab) {
                0 -> SearchTabContent(
                    countryInput = countryInput,
                    onCountryInputChange = { countryInput = it },
                    searchState = searchState,
                    onSearch = {
                        vm.searchCountry(countryInput.trim())
                        keyboardController?.hide()
                    },
                    onRetry = { vm.retry() },
                    onCountryClick = onCountryClick,
                    keyboardController = keyboardController
                )
                1 -> GlobalTabContent(
                    globalState = globalState,
                    onRefresh = { vm.loadGlobalSnapshot() },
                    onCountryClick = onCountryClick
                )
            }
        }
    }
}

@Composable
private fun SearchTabContent(
    countryInput: String,
    onCountryInputChange: (String) -> Unit,
    searchState: UiState,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onCountryClick: (String) -> Unit,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Search Country Statistics",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = countryInput,
            onValueChange = onCountryInputChange,
            label = { Text("Country name") },
            placeholder = { Text("e.g., Mexico, USA, Japan") },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (countryInput.isNotBlank()) {
                        onSearch()
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onSearch,
            enabled = countryInput.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Search, contentDescription = null)
            Spacer(Modifier.padding(4.dp))
            Text("Search")
        }

        Spacer(Modifier.height(24.dp))

        when (val currentState = searchState) {
            UiState.Idle -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔍 Enter a country name to search",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Try: Mexico, United States, Japan, Spain, Germany",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            UiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Searching for $countryInput...")
                }
            }

            is UiState.Error -> {
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
                            onClick = onRetry,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Try Again")
                        }
                    }
                }
            }

            is UiState.SuccessCountry -> {
                Column {
                    Text(
                        text = "📊 Results for \"$countryInput\"",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${currentState.data.size} region(s) found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(Modifier.height(16.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentState.data) { regionCase ->
                            RegionCard(
                                regionCase = regionCase,
                                onClick = { onCountryClick(regionCase.country) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlobalTabContent(
    globalState: UiStateGlobal,
    onRefresh: () -> Unit,
    onCountryClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🌍 Global Snapshot",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Quick view of multiple countries",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        when (val currentState = globalState) {
            UiStateGlobal.Idle -> {
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
                            text = "🌐",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Load Global Data",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "View COVID-19 statistics from multiple countries",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onRefresh) {
                            Text("Load Data")
                        }
                    }
                }
            }

            UiStateGlobal.Loading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Loading global data...")
                }
            }

            is UiStateGlobal.Error -> {
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
                            text = currentState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onRefresh,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            is UiStateGlobal.Success -> {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "📊 ${currentState.data.size} Countries",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Latest available data",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        OutlinedButton(onClick = onRefresh) {
                            Text("🔄")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Group by country and show aggregated data
                    val countryMap = currentState.data.groupBy { it.country }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(countryMap.keys.toList()) { country ->
                            val regions = countryMap[country] ?: emptyList()
                            GlobalCountryCard(
                                country = country,
                                regions = regions,
                                onClick = { onCountryClick(country) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlobalCountryCard(
    country: String,
    regions: List<RegionCases>,
    onClick: () -> Unit
) {
    val totalCases = regions.sumOf { region ->
        region.timeline.lastOrNull()?.total ?: 0
    }
    val totalNew = regions.sumOf { region ->
        region.timeline.lastOrNull()?.new ?: 0
    }
    val latestDate = regions.firstOrNull()?.timeline?.lastOrNull()?.date

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                    text = country,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total Cases",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = NumberFormat.getNumberInstance(Locale.US).format(totalCases),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "New Cases",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "+${NumberFormat.getNumberInstance(Locale.US).format(totalNew)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (totalNew > 0) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${regions.size} region${if (regions.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (latestDate != null) {
                        Text(
                            text = "Updated: $latestDate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(Modifier.padding(8.dp))

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RegionCard(
    regionCase: RegionCases,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    text = regionCase.country,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                if (regionCase.region != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Region: ${regionCase.region}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(8.dp))

                val latestData = regionCase.timeline.lastOrNull()
                if (latestData != null) {
                    Text(
                        text = "Latest: ${NumberFormat.getNumberInstance(Locale.US).format(latestData.total)} cases",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Date: ${latestData.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                } else {
                    Text(
                        text = "No data available",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}