package com.bytemanager.stats.utils

import android.os.BatteryManager
import com.bytemanager.stats.data.types.TimestampedBatteryState
import com.bytemanager.stats.data.types.TimestampedRawBatteryState

object BatteryStateRepositoryUtils {
    fun getBatteryLevelPercentage(level: Int?, scale: Int?): Int? {
        if(level == null || scale == null) {
            return null
        }

        return if(level >= 0 && scale > 0  && level <= scale) {
            (level * 100) / scale
        } else {
            null
        }
    }

    fun getTemperatureInCelsius(rawValue: Int?): Float? {
        return if(rawValue != null) (rawValue / 10f) else null
    }

    fun getVoltage(rawValue: Int?): Float? {
        return if(rawValue != null) (rawValue / 1000f) else null
    }

    fun getTechnology(rawString: String?): String {
        val technology = rawString ?: "Not available"
        return technology
    }

    fun getBatteryHealth(rawValue: Int?): String {
        val health = when(rawValue) {
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheated"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Overvoltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> "Unknown"
            else -> "Not available"
        }

        return health
    }

    fun getChargingStatus(rawValue: Int?): String {
        val chargingStatus = when(rawValue) {
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
            BatteryManager.BATTERY_STATUS_UNKNOWN -> "Unknown"
            else -> "Not available"
        }

        return chargingStatus
    }

    fun convertTimestampedBatteryStateToBatteryState(
        timestampedRawBatteryState: TimestampedRawBatteryState
    ): TimestampedBatteryState {
        return TimestampedBatteryState(
            timestamp = timestampedRawBatteryState.timestamp,
            level = getBatteryLevelPercentage(timestampedRawBatteryState.level, timestampedRawBatteryState.scale),
            voltage = getVoltage(timestampedRawBatteryState.voltage),
            temperature = getTemperatureInCelsius(timestampedRawBatteryState.temperature),
            chargingStatus = getChargingStatus(timestampedRawBatteryState.chargingStatus),
            batteryHealth = getBatteryHealth(timestampedRawBatteryState.batteryHealth),
        )
    }
}