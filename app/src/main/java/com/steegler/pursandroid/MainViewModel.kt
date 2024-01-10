package com.steegler.pursandroid

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steegler.pursandroid.di.DependencyProvider
import com.steegler.pursandroid.network.PursAPI
import com.steegler.pursandroid.repo.PursRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

class MainViewModel : ViewModel() {

    private val isPlayground = false

    private val repo by lazy { if (isPlayground) DependencyProvider.instance.localRepo else DependencyProvider.instance.repo }


    private var _items: MutableStateFlow<Map<DayOfWeek, List<List<LocalTime>>>> = MutableStateFlow(emptyMap())
    val items = _items.asStateFlow()

    private var _title: MutableStateFlow<String> = MutableStateFlow("")
    val title = _title.asStateFlow()

    fun requestData() {
        viewModelScope.launch {
            repo.fetchData().let { (a, b) ->
                _title.value = a
                _items.value = b
            }
        }
    }

    fun getColor(): Color {
        getHeaderText()
        val isOpen = isOpen()
        return when {
            !isOpen -> Color.Red
            isOpen -> {
                val max = items.value.filter { it.key.isToday }.firstNotNullOfOrNull { it.value }?.map { Duration.between(LocalTime.now(), it.last()).toMinutes() }!!.max()
                if (max > 60) Color.Green else Color.Yellow
            }

            else -> Color.Red
        }
    }

    fun getHeaderText(): String {
        val schedule = items.value
        val minutesToMidnight = Duration.between(LocalTime.now(), LocalTime.MIDNIGHT).toMinutes()
        val nextPeriod = findNextTimePeriod()
        println(items.value.filter { it.key.isToday })

        if (nextPeriod.second.isEmpty())
            return ""

        var mins = 0L

        if (isOpen()) {
            mins =
                (schedule.filter { it.key.isToday }.firstNotNullOf { it.value }.first { LocalTime.now() in (it.first()..it.last()) }.last())
                    .let { Duration.between(LocalTime.now(), it).toMinutes() }
        } else {
            if (nextPeriod.first == 0) {
                val upToStart = nextPeriod.second.firstOrNull()?.let { Duration.between(LocalTime.now(), it).toMinutes() } ?: 0
                mins -= upToStart
            } else {
                mins -= minutesToMidnight
                mins -= (nextPeriod.first * 24 * 60)
                val upToStart = nextPeriod.second.firstOrNull()?.let { Duration.between(LocalTime.MIN, it).toMinutes() } ?: 0
                mins -= upToStart
            }
        }

        val printableFormat = DateTimeFormatter.ofPattern("hh:mm a")

        return when (mins) {
            in 1..60 -> {
                "Open until ${printableFormat.format(getActivePeriod()?.last())}, " +
                        "reopens at ${printableFormat.format(nextPeriod.second.first())}"
            }

            in 61.rangeUntil(Long.MAX_VALUE) -> {
                println(nextPeriod)
                getActivePeriod()?.last()?.let { return "Open until ${printableFormat.format(it)}" } ?: "---$mins---"
            }

            in (-24 * 60)..0 -> {
                "Opens again at ${printableFormat.format(nextPeriod.second.first())}"
            }

            in Long.MIN_VALUE.rangeUntil(-24 * 60) -> {
                "Opens ${nextPeriod.third.fullName} " +
                        printableFormat.format(nextPeriod.second.first())
            }

            else -> "Something went wrong"
        }

    }

    private fun getActivePeriod(time: LocalTime = LocalTime.now()): List<LocalTime>? =
        items.value.filter { it.key.isToday }.firstNotNullOfOrNull { it.value }?.firstOrNull { time in (it.first()..it.last()) }

    private fun isOpen(time: LocalTime = LocalTime.now()): Boolean =
        items.value.filter { it.key.isToday }.firstNotNullOfOrNull { it.value }?.map { time in (it.first()..it.last()) }?.contains(true) ?: false

    private fun findNextTimePeriod(after: LocalTime = LocalTime.now()): Triple<Int, List<LocalTime>, DayOfWeek> {
        val index = DayOfWeek.entries.indexOfFirst { it.isToday }
        val tail = DayOfWeek.entries.subList(index, DayOfWeek.entries.size)
        val head = DayOfWeek.entries.subList(0, index)
        val totalPeriods = items.value.mapValues { it.value }.flatMap { it.value }.size
        var period: List<LocalTime> = listOf()


        (tail + head).forEachIndexed { i, weekday ->
            items.value[weekday]?.forEach {
                if (weekday.isToday && totalPeriods > 1 && after.isBefore(it.first()))
                    return Triple(i, it, weekday)
                else if (period.isEmpty())
                    period = it
            }
        }
        return Triple(6, period, DayOfWeek.getToday) // could be an issue
    }
}


class Playground : PursAPI, PursRepo {

    private val format = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }


    fun invoke(): Pair<String, Map<DayOfWeek, List<List<LocalTime>>>> {
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
            .withResolverStyle(ResolverStyle.LENIENT)

        val response = format.decodeFromString(LocationItemResponse.serializer(), json)
        val grouped = response.hours.sortedBy { it.day }
            .groupBy(
                keySelector = { it.day },
                valueTransform = { listOf(LocalTime.parse(it.start, timeFormatter), LocalTime.parse(it.end, timeFormatter)) })

        return Pair(response.name, grouped)
    }

    override suspend fun getData(): LocationItemResponse {
        return format.decodeFromString(LocationItemResponse.serializer(), json)
    }

    override suspend fun fetchData(): Pair<String, Map<DayOfWeek, List<List<LocalTime>>>> {
        val response = getData()
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
            .withResolverStyle(ResolverStyle.LENIENT)
        val grouped = response.hours.sortedBy { it.day }
            .groupBy(
                keySelector = { it.day },
                valueTransform = { listOf(LocalTime.parse(it.start, timeFormatter), LocalTime.parse(it.end, timeFormatter)) })

        return Pair(response.name, grouped)
    }

}