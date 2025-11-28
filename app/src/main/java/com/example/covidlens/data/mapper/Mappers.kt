package com.example.covidlens.data.mapper

import com.example.covidlens.data.remote.dto.CaseDto
import com.example.covidlens.data.remote.dto.RegionStatsDto
import com.example.covidlens.domain.model.CaseCount
import com.example.covidlens.domain.model.RegionCases

fun RegionStatsDto.toDomain(): RegionCases {
    return RegionCases(
        country = country,
        region = region,
        timeline = cases.toTimelineList()
    )
}

fun Map<String, CaseDto>.toTimelineList(): List<CaseCount> {
    return entries
        .map { (date, dto) ->
            CaseCount(
                date = date,
                total = dto.total,
                new = dto.new
            )
        }
        .sortedBy { it.date }
}
