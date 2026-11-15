package com.bytemanager.stats.data.types

data class TimestampedThermalState(
    val timestamp: Long,
    val throttlingStatus: Int? = null,
)
