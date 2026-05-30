package com.bytemanager.stats.data_structure

data class BatteryState(
    val timestamp: Long,
    val level: Int,
    val voltage: Float,
    val chargingStatus: String,
    val temperature: Float,
    val technology: String,
    val health: String
)