package com.example.covidlens.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.covidlens.domain.model.RegionCases

@Composable
fun CountrySnapshotCard(region: RegionCases, onClick: () -> Unit) {
    val latest = region.timeline.lastOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(region.country, style = MaterialTheme.typography.titleLarge)
            latest?.let {
                Spacer(Modifier.height(8.dp))
                Text("Latest date: ${it.date}")
                Text("New cases: ${it.new}")
                Text("Total cases: ${it.total}")
            }
        }
    }
}
