package com.bytemanager.stats.data.types

data class TimestampedBatteryLevel(
    val timestamp: Long,
    val level: Int
)
