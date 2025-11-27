package com.example.covidlens.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap

@Composable
fun TrendChart(
    values: List<Int>,
    label: String? = null
) {
    if (values.isEmpty()) return

    val max = values.maxOrNull() ?: 1
    val normalized = values.map { it.toFloat() / max }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val w = size.width
        val h = size.height

        val step = w / (values.size - 1)

        for (i in 0 until values.lastIndex) {
            val x1 = i * step
            val y1 = h - (normalized[i] * h)

            val x2 = (i + 1) * step
            val y2 = h - (normalized[i + 1] * h)

            drawLine(
                color = Color(0xFF0057FF), // Azul estilo Stripe
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
        }
    }
}
