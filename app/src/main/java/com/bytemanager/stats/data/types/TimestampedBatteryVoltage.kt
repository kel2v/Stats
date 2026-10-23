package com.bytemanager.stats.data.types

data class TimestampedBatteryVoltage(
    val timestamp: Long,
    val voltage: Float
)