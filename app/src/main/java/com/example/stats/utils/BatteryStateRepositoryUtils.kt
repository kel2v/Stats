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
}