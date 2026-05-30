package com.bytemanager.stats.utils

import com.bytemanager.stats.data_structure.TimestampInterval
import java.time.LocalDateTime
import java.time.ZoneId

class TimestampIntervalUtils {
    fun convertDayIntoTimestampBasedInterval(
        year: Int,
        month: Int,
        day: Int,
        zoneId: ZoneId
    ): TimestampInterval {
        val startTimestamp = LocalDateTime
            .of(year, month, day, 0, 0, 0)
            .atZone(zoneId)
            .toEpochSecond()

        val endTimestamp = LocalDateTime
            .of(year, month, day, 23, 59, 59)
            .atZone(zoneId)
            .toEpochSecond()

        return TimestampInterval(startTimestamp, endTimestamp)
    }
}