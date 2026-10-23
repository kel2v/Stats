package com.bytemanager.stats.data.types

import com.bytemanager.stats.utils.BatteryStateRepositoryUtils

data class TimestampedRawBatteryLevel (
    val timestamp: Long,
    val level: Int?,
    val scale: Int?
)

fun TimestampedRawBatteryLevel.convertToTimestampedBatteryLevel(): TimestampedBatteryLevel? {
    val convertedValue = BatteryStateRepositoryUtils.getBatteryLevelPercentage(
        level = this.level,
        scale = this.scale
    ) ?: return null

    return TimestampedBatteryLevel(
        timestamp = this.timestamp,
        level = convertedValue
    )
}