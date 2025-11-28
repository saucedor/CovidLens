package com.example.covidlens.ui.util

import java.text.Normalizer
import java.util.Locale

private val knownTranslations = mapOf(
    "estados unidos" to "united states",
    "reino unido" to "united kingdom",
    "inglaterra" to "united kingdom",
    "eeuu" to "united states",
    "ee.uu" to "united states",
    "corea del sur" to "south korea",
    "corea del norte" to "north korea",
    "alemania" to "germany",
    "españa" to "spain",
    "mexico" to "mexico",
    "méxico" to "mexico",
    "peru" to "peru",
    "perú" to "peru",
    "brasil" to "brazil",
    "argentina" to "argentina",
    "colombia" to "colombia",
    "chile" to "chile",
    "canada" to "canada",
    "canadá" to "canada",
    "italia" to "italy"
)

fun String.toApiCountryQuery(): String {
    val trimmed = trim()
    if (trimmed.isBlank()) return ""

    val ascii = Normalizer.normalize(trimmed, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase(Locale.US)

    val replacement = knownTranslations[ascii] ?: ascii

    return replacement.split(Regex("\\s+"))
        .joinToString(" ") { word ->
            if (word.isBlank()) ""
            else word.replaceFirstChar { c ->
                if (c.isLowerCase()) c.titlecase(Locale.US) else c.toString()
            }
        }.trim()
}
