package com.steegler.pursandroid

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId


val json = """
    {
        "location_name": "BEASTRO by Marshawn Lynch",
        "hours": [
            {
                "day_of_week": "WED",
                "start_local_time": "01:00:00",
                "end_local_time": "02:30:00"
            },
            {
                "day_of_week": "WED",
                "start_local_time": "15:00:00",
                "end_local_time": "22:00:00"
            },
            {
                "day_of_week": "SAT",
                "start_local_time": "10:00:00",
                "end_local_time": "24:00:00"
            },
            {
                "day_of_week": "SUN",
                "start_local_time": "00:00:00",
                "end_local_time": "02:00:00"
            },
            {
                "day_of_week": "SUN",
                "start_local_time": "10:30:00",
                "end_local_time": "21:00:00"
            },
            {
                "day_of_week": "TUE",
                "start_local_time": "07:00:00",
                "end_local_time": "13:00:00"
            },
            {
                "day_of_week": "TUE",
                "start_local_time": "15:00:00",
                "end_local_time": "23:59:00"
            },
            {
                "day_of_week": "THU",
                "start_local_time": "00:00:00",
                "end_local_time": "24:00:00"
            },
            {
                "day_of_week": "FRI",
                "start_local_time": "07:00:00",
                "end_local_time": "24:00:00"
            }
        ]
    }
""".trimIndent()

@Serializable
data class LocationItemResponse(

    @SerialName("hours")
    val hours: List<WorkingHours>,
    @SerialName("location_name")
    val name: String
)

@Serializable
data class WorkingHours(
    @SerialName("day_of_week")
    val day: DayOfWeek,

    @SerialName("end_local_time")
    val end: String,

    @SerialName("start_local_time")
    val start: String
)

@Serializable
enum class DayOfWeek {
    SUN, MON, TUE, WED, THU, FRI, SAT;

    val fullName: String
        get() = when (this) {
            MON -> "Monday"
            TUE -> "Tuesday"
            WED -> "Wednesday"
            THU -> "Thursday"
            FRI -> "Friday"
            SAT -> "Saturday"
            SUN -> "Sunday"
        }
    val isToday: Boolean
        get() = this.ordinal == Instant.now().atZone(ZoneId.systemDefault()).dayOfWeek.value

    companion object {

        @JvmStatic
        val getToday = DayOfWeek.entries.first { it.isToday }


    }
}
