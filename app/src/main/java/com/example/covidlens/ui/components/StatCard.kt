package com.example.covidlens.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatCard(total: Int, newCases: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Total cases", style = MaterialTheme.typography.labelMedium)
                Text(text = "%,d".format(total), style = MaterialTheme.typography.displaySmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "New cases", style = MaterialTheme.typography.labelMedium)
                Text(text = "+%,d".format(newCases), style = MaterialTheme.typography.displaySmall)
            }
        }
    }
}