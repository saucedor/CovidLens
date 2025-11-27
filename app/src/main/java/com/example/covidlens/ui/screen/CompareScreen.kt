package com.example.covidlens.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.covidlens.ui.state.UiState
import com.example.covidlens.ui.viewmodel.MainViewModel

@Composable
fun CompareScreen(vm: MainViewModel) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
    ) {
        when (val s = state) {
            is UiState.SuccessComparison -> {
                s.data.forEach { stats ->
                    Text(stats.country, style = MaterialTheme.typography.headlineSmall)
                    // Add more details here
                }
            }
            is UiState.Error -> {
                Text(s.message, color = MaterialTheme.colorScheme.error)
            }
            UiState.Loading -> Text("Loading...")
            else -> {}
        }
    }
}
