package com.bytemanager.stats.data.types

data class TimestampedBatteryState(
    val timestamp: Long? = null,
    val level: Int? = null,
    val voltage: Float? = null,
    val temperature: Float? = null,
    val chargingStatus: String? = null,
    val batteryHealth: String? = null,
    val technology: String? = null,
)