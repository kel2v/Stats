package com.example.stats.data

data class BatteryState(
    val level: Int,
    val voltage: Int,
    val chargingStatus: Int,
    val temperature: Int,
    val technology: String?,
    val health: Int
)