package com.bytemanager.stats.data.types

import com.bytemanager.stats.utils.BatteryStateRepositoryUtils

data class TimestampedRawBatteryTemp(
    val timestamp: Long,
    val temperature: Int?
)

fun TimestampedRawBatteryTemp.convertToTimestampedBatteryTemp(): TimestampedBatteryTemp? {
    val convertedValue = BatteryStateRepositoryUtils.getTemperatureInCelsius(this.temperature) ?: return null

    return TimestampedBatteryTemp(
        timestamp = this.timestamp,
        temperature = convertedValue
    )
}