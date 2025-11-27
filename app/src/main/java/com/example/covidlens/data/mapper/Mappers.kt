package com.example.covidlens.data.mapper

import com.example.covidlens.data.remote.dto.RegionTimelineDto
import com.example.covidlens.domain.model.CountryStats
import com.example.covidlens.domain.model.DayStat

fun List<RegionTimelineDto>.toDomain(): CountryStats {
    val countryName = this.firstOrNull()?.country ?: "Unknown"

    // A map to hold the aggregated stats for each day (date -> DayStat)
    val aggregatedStats = mutableMapOf<String, DayStat>()

    // Iterate over each region's timeline
    this.forEach { regionDto ->
        regionDto.cases.forEach { (date, caseDayDto) ->
            val existingDayStat = aggregatedStats[date]
            if (existingDayStat != null) {
                // If the date already exists, add the new cases to the total
                aggregatedStats[date] = existingDayStat.copy(
                    confirmed = (existingDayStat.confirmed ?: 0) + caseDayDto.total
                    // The new API response does not have deaths or recovered per day, so we leave them as null
                )
            } else {
                // If the date does not exist, create a new entry
                aggregatedStats[date] = DayStat(
                    date = date,
                    confirmed = caseDayDto.total,
                    deaths = null, // Not provided in the new API response
                    recovered = null // Not provided in the new API response
                )
            }
        }
    }

    val timeline = aggregatedStats.values.sortedBy { it.date }

    return CountryStats(country = countryName, timeline = timeline)
}
