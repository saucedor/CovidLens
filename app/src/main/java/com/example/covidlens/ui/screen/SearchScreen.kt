package com.example.covidlens.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.covidlens.ui.state.UiState
import com.example.covidlens.ui.viewmodel.MainViewModel

@Composable
fun SearchScreen(
    vm: MainViewModel,
    navigateToDetail: () -> Unit,
    navigateToCompare: () -> Unit
) {
    val state by vm.state.collectAsState()

    var country by remember { mutableStateOf("") }
    var lastCountry by remember { mutableStateOf<String?>(null) }

    // --- Side Effects ---
    // 1. Load the last searched country when the screen is first displayed
    LaunchedEffect(Unit) {
        vm.loadLastCountry { loadedCountry ->
            if (loadedCountry != null) {
                lastCountry = loadedCountry
                country = loadedCountry // Pre-fill the search field
            }
        }
    }

    // 2. Navigate to detail screen only when the search is successful
    LaunchedEffect(state) {
        if (state is UiState.SuccessCountry) {
            navigateToDetail()
        }
    }

    // --- UI Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
    ) {
        Text("CovidLens", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(30.dp))

        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            label = { Text("País") },
            modifier = Modifier.fillMaxWidth(),
            isError = state is UiState.Error
        )

        // --- State-dependant UI ---
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)) {
            when (val s = state) {
                is UiState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center)) {
                        Text(s.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { vm.retry() }) {
                            Text("Reintentar")
                        }
                    }
                }
                UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    // Show the last country suggestion if there's no active loading/error
                    lastCountry?.let {
                        Column(modifier = Modifier.align(Alignment.Center)) {
                            Text("Último país buscado:")
                            TextButton(onClick = { country = it }) {
                                Text(it)
                            }
                        }
                    }
                }
            }
        }

        // --- Action Buttons ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = vm.simulatedMode, onCheckedChange = { vm.simulatedMode = it })
            Spacer(Modifier.width(8.dp))
            Text("Modo simulado")
        }
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { vm.searchCountry(country) }, // Only trigger the search
            modifier = Modifier.fillMaxWidth(),
            enabled = state != UiState.Loading
        ) {
            Text("Buscar")
        }
        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = navigateToCompare,
            modifier = Modifier.fillMaxWidth(),
            enabled = state != UiState.Loading
        ) {
            Text("Comparar países")
        }
    }
}
