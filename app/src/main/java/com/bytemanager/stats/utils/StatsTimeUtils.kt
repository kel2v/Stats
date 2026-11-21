package com.bytemanager.stats.utils

import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import com.bytemanager.stats.data.types.TimestampInterval
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.TimeZone

class StatsTimeUtils(
    private val clock: Clock = Clock.systemDefaultZone()
) {
    fun currentEpochSecond(): Long {
        return Instant.now(clock).epochSecond
    }

    fun currentTime(): LocalTime {
        return LocalTime.now(clock)
    }

    fun currentDate(): LocalDate {
        return LocalDate.now(clock)
    }

    fun epochMillisToLocalDate(millis: Long?): LocalDate? {
        if(millis == null) {
            return null
        }

        val calendar = Calendar.getInstance(TimeZone.getTimeZone(clock.zone))
        calendar.timeInMillis = millis

        return calendar.toInstant().atZone(clock.zone).toLocalDate()
    }

    fun localDateToStartOfTheDayInEpochSeconds(localDate: LocalDate): Long {
        return localDate.atStartOfDay(clock.zone).toEpochSecond()
    }

    fun localDateToTimestampInterval(localDate: LocalDate): TimestampInterval {
        val startTimestamp = localDate.atStartOfDay(clock.zone).toEpochSecond()
        val endTimestamp = localDate.plusDays(1).atStartOfDay(clock.zone).toEpochSecond()

        return TimestampInterval(startTimestamp, endTimestamp)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun dateRangeToTimestampInterval(dateRangePickerState: DateRangePickerState): TimestampInterval? {
        val startLocalDate = dateRangePickerState.getSelectedStartDate()
        val endLocalDate = dateRangePickerState.getSelectedEndDate()

        var extractedTimestampInterval: TimestampInterval? = null
        if(startLocalDate != null && endLocalDate != null) {
            val timestampIntervalOfStartDate = localDateToTimestampInterval(startLocalDate)
            val timestampIntervalOfEndDate = localDateToTimestampInterval(endLocalDate)

            extractedTimestampInterval = TimestampInterval(
                timestampIntervalOfStartDate.startTimestamp,
                timestampIntervalOfEndDate.endTimestamp
            )
        }

        return extractedTimestampInterval
    }
}