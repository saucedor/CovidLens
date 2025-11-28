package com.example.covidlens.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.covidlens.ui.model.TimelineItem

@Composable
fun TrendChart(
    data: List<TimelineItem>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val maxValue = (data.maxOf { it.total }).takeIf { it > 0 } ?: 1
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(8.dp)
    ) {
        val gap = size.width / (data.size - 1).coerceAtLeast(1)
        val points = data.mapIndexed { index, item ->
            val x = index * gap
            val y = size.height - (item.total / maxValue.toFloat()) * size.height
            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach {
                lineTo(it.x, it.y)
            }
        }

        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 6f, cap = StrokeCap.Round)
        )
    }
}
