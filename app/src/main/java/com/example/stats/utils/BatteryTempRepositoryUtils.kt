package com.example.stats.utils

import com.example.stats.data.TimestampBasedTimeInterval
import java.time.LocalDateTime
import java.time.ZoneId

class BatteryTempRepositoryUtils {
    fun convertDayIntoTimestampBasedTimeRange(
        year: Int,
        month: Int,
        day: Int,
        zoneId: ZoneId
    ): TimestampBasedTimeInterval {
        val startTimestamp = LocalDateTime
            .of(year, month, day, 0, 0, 0)
            .atZone(zoneId)
            .toEpochSecond()

        val endTimestamp = LocalDateTime
            .of(year, month, day, 23, 59, 59)
            .atZone(zoneId)
            .toEpochSecond()

        return TimestampBasedTimeInterval(startTimestamp, endTimestamp)
    }
}