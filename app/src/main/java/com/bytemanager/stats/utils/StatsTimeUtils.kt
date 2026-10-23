package com.bytemanager.stats.utils

import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import com.bytemanager.stats.data.types.TimestampInterval
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

class StatsTimeUtils(val zoneId: ZoneId = ZoneId.systemDefault()) {
    fun localDayToTimestampInterval(localDate: LocalDate): TimestampInterval {
        val startTimestamp = localDate.atStartOfDay(zoneId).toEpochSecond()
        val endTimestamp = localDate.plusDays(1).atStartOfDay(zoneId).toEpochSecond()

        return TimestampInterval(startTimestamp, endTimestamp)
    }

    fun nowInTimestamp(): Long {
        return Instant.now().atZone(zoneId).toEpochSecond()
    }

    fun nowInLocalTime(): LocalTime {
        return Instant.now().atZone(zoneId).toLocalTime()
    }

    fun today(): LocalDate {
        val now = Instant.now().atZone(zoneId)
        val year = now.year
        val month = now.month
        val day = now.dayOfMonth

        return LocalDate.of(year, month, day)
    }

    fun millisToLocalDate(millis: Long?): LocalDate? {
        if(millis == null) {
            return null
        }

        val calendar = Calendar.getInstance(TimeZone.getTimeZone(zoneId))
        calendar.timeInMillis = millis

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        return LocalDate.of(year, month+1, dayOfMonth)
    }

    fun localDateToStartOfTheDayInEpochSeconds(localDate: LocalDate): Long {
        val result = localDate.atStartOfDay(zoneId).toEpochSecond()

        return result
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun convertDateRangeToTimestampInterval(dateRangePickerState: DateRangePickerState): TimestampInterval? {
        val startLocalDate = dateRangePickerState.getSelectedStartDate()
        val endLocalDate = dateRangePickerState.getSelectedEndDate()

        var extractedTimestampInterval: TimestampInterval? = null
        if(startLocalDate != null && endLocalDate != null) {
            extractedTimestampInterval = TimestampInterval(
                localDateToStartOfTheDayInEpochSeconds(startLocalDate),
                localDateToStartOfTheDayInEpochSeconds(endLocalDate) + 86400
            )
        }

        return extractedTimestampInterval
    }
}