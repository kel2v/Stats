package com.bytemanager.stats.utils

import android.os.BatteryManager
import androidx.compose.ui.text.intl.Locale
import com.bytemanager.stats.data.types.TimestampedBatteryState
import com.bytemanager.stats.data.types.TimestampedRawBatteryState
import kotlin.math.roundToInt

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


    fun createTimestampedBatteryState(
        timestampedRawBatteryState: TimestampedRawBatteryState,
        batteryTechnology: String,
    ): TimestampedBatteryState {
        return TimestampedBatteryState(
            timestamp = timestampedRawBatteryState.timestamp,
            level = getBatteryLevelPercentage(timestampedRawBatteryState.level, timestampedRawBatteryState.scale),
            voltage = getVoltage(timestampedRawBatteryState.voltage),
            temperature = getTemperatureInCelsius(timestampedRawBatteryState.temperature),
            chargingStatus = getChargingStatus(timestampedRawBatteryState.chargingStatus),
            batteryHealth = getBatteryHealth(timestampedRawBatteryState.batteryHealth),
            technology = batteryTechnology
        )
    }








    fun getBatteryLevelPercentageVerbose(timestampedBatteryState: TimestampedBatteryState, shorter: Boolean = false): String {
        timestampedBatteryState.level ?: return "-"
        val detailed = "${timestampedBatteryState.level} %"
        val short = "${timestampedBatteryState.level}%"

        return if(shorter) short else detailed
    }

    fun getVoltageVerbose(timestampedBatteryState: TimestampedBatteryState, shorter: Boolean = false): String {
        timestampedBatteryState.voltage ?: return "-"
        val precision = if(shorter) "%.1f" else "%.3f"
        val formattedValue = String.format(Locale.current.platformLocale, precision, timestampedBatteryState.voltage)

        val detailed = "$formattedValue V"
        val short = "${formattedValue}V"

        return if(shorter) short else detailed
    }

    fun getTemperatureVerbose(timestampedBatteryState: TimestampedBatteryState, showInFahrenheits: Boolean, shorter: Boolean = false): String {
        timestampedBatteryState.temperature ?: return "-"

        val value = if(showInFahrenheits){
            convertCelsiusToFahrenheits(timestampedBatteryState.temperature)
        } else {
            timestampedBatteryState.temperature
        }

        val unitSymbol = if(showInFahrenheits) "F" else "C"

        val formatedValue = String.format(Locale.current.platformLocale, "%.1f", value)

        val detailed = "$formatedValue °$unitSymbol"
        val short = "${value.roundToInt()}°"

        return if(shorter) short else detailed
    }

    fun convertCelsiusToFahrenheits(tempInCelsius: Float): Float {
        return (tempInCelsius * 1.8f) + 32f
    }
}