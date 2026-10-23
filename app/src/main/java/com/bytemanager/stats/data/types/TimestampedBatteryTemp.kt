package com.bytemanager.stats.data.types

data class TimestampedBatteryTemp(
    val timestamp: Long,
    val temperature: Float
)