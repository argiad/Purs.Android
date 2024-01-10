package com.steegler.pursandroid.repo

import com.steegler.pursandroid.DayOfWeek
import com.steegler.pursandroid.network.PursAPI
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

interface PursRepo {
    suspend fun fetchData(): Pair<String, Map<DayOfWeek, List<List<LocalTime>>>>
}

class PursRepoImpl(private val api: PursAPI) : PursRepo {
    override suspend fun fetchData(): Pair<String, Map<DayOfWeek, List<List<LocalTime>>>> {
        val response = api.getData()
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n$response")
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
            .withResolverStyle(ResolverStyle.LENIENT)
        val grouped = response.hours.sortedBy { it.day }
            .groupBy(
                keySelector = { it.day },
                valueTransform = { listOf(LocalTime.parse(it.start, timeFormatter), LocalTime.parse(it.end, timeFormatter)) })

        return Pair(response.name, grouped)
    }

}