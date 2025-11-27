package com.example.covidlens.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatCard(label: String, value: Int?) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(90.dp),
        elevation = CardDefaults.elevatedCardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                (value ?: 0).toString(),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
