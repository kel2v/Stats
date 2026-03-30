package com.example.stats.data

data class BatteryState(
    val level: Int,
    val voltage: Float,
    val chargingStatus: String,
    val temperature: Float,
    val technology: String,
    val health: String
)