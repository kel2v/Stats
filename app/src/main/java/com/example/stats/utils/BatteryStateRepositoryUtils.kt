package com.example.stats.utils

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
}