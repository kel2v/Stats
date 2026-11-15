package com.bytemanager.stats.utils

import android.os.PowerManager

object ThermalStateRepositoryUtils {
    fun getThermalThrottlingStatusVerbose(status: Int?): String {
        return when (status) {
            PowerManager.THERMAL_STATUS_NONE -> "No throttling"
            PowerManager.THERMAL_STATUS_LIGHT -> "Light throttling"
            PowerManager.THERMAL_STATUS_MODERATE -> "Moderate throttling"
            PowerManager.THERMAL_STATUS_SEVERE -> "Severe throttling"
            PowerManager.THERMAL_STATUS_CRITICAL -> "Critical throttling"
            PowerManager.THERMAL_STATUS_EMERGENCY -> "Emergency throttling"
            PowerManager.THERMAL_STATUS_SHUTDOWN -> "NEED IMMEDIATE SHUTDOWN"
            else -> "Unknown"
        }
    }

    fun getThermalThrottlingStatusShortString(status: Int?): String {
        return when (status) {
            PowerManager.THERMAL_STATUS_NONE -> "NT"
            PowerManager.THERMAL_STATUS_LIGHT -> "LT"
            PowerManager.THERMAL_STATUS_MODERATE -> "MT"
            PowerManager.THERMAL_STATUS_SEVERE -> "ST"
            PowerManager.THERMAL_STATUS_CRITICAL -> "CT"
            PowerManager.THERMAL_STATUS_EMERGENCY -> "E!"
            PowerManager.THERMAL_STATUS_SHUTDOWN -> "!!"
            else -> "-"
        }
    }
}