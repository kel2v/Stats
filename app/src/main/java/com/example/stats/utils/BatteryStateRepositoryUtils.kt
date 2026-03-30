package com.example.stats.utils

import android.os.BatteryManager

class BatteryStateRepositoryUtils {
    fun getBatteryLevelPercentage(level: Int, scale: Int): Int {
        val percentage = if(level >= 0 && scale > 0  && level <= scale) {
            (level * 100) / scale
        } else {
            -1
        }

        return percentage
    }

    fun getTemperatureInCelsius(rawValue: Int): Float {
        val tempInCelsius = if(rawValue != Int.MIN_VALUE) {
            rawValue / 10f
        } else {
            Float.MAX_VALUE * -1
        }

        return tempInCelsius
    }

    fun getVoltage(rawValue: Int): Float {
        val voltage = if(rawValue != Int.MIN_VALUE) {
            rawValue / 1000f
        } else {
            Float.MAX_VALUE * -1
        }

        return voltage
    }

    fun getTechnology(rawString: String?): String {
        val technology = rawString ?: "Not available"
        return technology
    }

    fun getHealth(rawValue: Int): String {
        val health = when(rawValue) {
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheated"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Overvoltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> "Unknown"
            else -> "Unknown"
        }

        return health
    }
}