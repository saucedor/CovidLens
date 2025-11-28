package com.example.covidlens.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.covidlens.ui.state.UiState
import com.example.covidlens.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    vm: MainViewModel,
    navigateToDetail: () -> Unit,
    navigateToCompare: () -> Unit
) {
    val state by vm.state.collectAsState()

    var country by rememberSaveable { mutableStateOf("") }
    var lastCountry by rememberSaveable { mutableStateOf<String?>(null) }

    val quickPicks = remember {
        listOf("Colombia", "Mexico", "Argentina", "USA", "Spain", "Italy", "Germany", "Japan")
    }

    LaunchedEffect(Unit) {
        vm.loadLastCountry { loadedCountry ->
            if (loadedCountry != null) {
                lastCountry = loadedCountry
                country = loadedCountry // Pre-fill the search field
            }
        }
    }

    LaunchedEffect(state) {
        if (state is UiState.SuccessCountry) {
            navigateToDetail()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("CovidLens", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(30.dp))

        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            label = { Text("Country") },
            placeholder = { Text("Enter a country name (e.g., Colombia)") },
            modifier = Modifier.fillMaxWidth(),
            isError = state is UiState.Error,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            singleLine = true
        )
        Spacer(Modifier.height(24.dp))

        Text("Quick picks", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickPicks.forEach { pick ->
                AssistChip(onClick = { country = pick }, label = { Text(pick) })
            }
        }
        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val s = state) {
                is UiState.Error -> {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("We couldn't complete the search", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(s.message, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { vm.retry() }) {
                                Text("Try again")
                            }
                        }
                    }
                }
                UiState.Loading -> CircularProgressIndicator()
                else -> {}
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { vm.searchCountry(country) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state != UiState.Loading
        ) {
            Text("Search")
        }
        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = navigateToCompare,
            modifier = Modifier.fillMaxWidth(),
            enabled = state != UiState.Loading
        ) {
            Text("Compare countries")
        }
    }
}
