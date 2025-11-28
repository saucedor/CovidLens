package com.example.covidlens.ui.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

fun List<DayStat>.averageGrowth(): Int {
    if (size <= 1) return 0
    val first = first().confirmed ?: 0
    val last = last().confirmed ?: 0
    val delta = last - first
    return (delta / (size - 1)).coerceAtLeast(0)
}

fun String.toReadableDate(): String {
    return try {
        val parsed = LocalDate.parse(this)
        parsed.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
    } catch (_: DateTimeParseException) {
        this
    }
}
