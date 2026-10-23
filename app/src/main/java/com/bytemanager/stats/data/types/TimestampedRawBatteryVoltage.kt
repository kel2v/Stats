package com.bytemanager.stats.data.types

import com.bytemanager.stats.utils.BatteryStateRepositoryUtils

data class TimestampedRawBatteryVoltage(
    val timestamp: Long,
    val voltage: Int?
)

fun TimestampedRawBatteryVoltage.convertToTimestampedBatteryVoltage(): TimestampedBatteryVoltage? {
    val convertedValue = BatteryStateRepositoryUtils.getVoltage(this.voltage) ?: return null

    return TimestampedBatteryVoltage(
        timestamp = this.timestamp,
        voltage = convertedValue
    )
}