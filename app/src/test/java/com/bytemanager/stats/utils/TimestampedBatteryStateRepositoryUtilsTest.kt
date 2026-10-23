package com.bytemanager.stats.utils

import android.os.BatteryManager
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class TimestampedBatteryStateRepositoryUtilsTest {
    @ParameterizedTest(name = "level={0}, scale={1} -> {2}%")
    @CsvSource(
        " , 0",

        "0, ",

        " , ",
        )
    fun getBatteryLevelPercentage_nullInput_returnsNull(level: Int?, scale: Int?) {
        assertNull(BatteryStateRepositoryUtils.getBatteryLevelPercentage(level, scale))
    }


    @ParameterizedTest(name = "level={0}, scale={1} -> {2}%")
    @CsvSource(
        "0,1,0",
        "1,1,100",

        "0,2,0",
        "1,2,50",
        "2,2,100",

        "0,3,0",
        "1,3,33",
        "2,3,66",
        "3,3,100",

        "0,5,0",
        "1, 5, 20",
        "2, 5, 40",
        "3, 5, 60",
        "4,5,80",
        "5,5,100",

        "9,10,90",
        "10,10,100",
        "0,100,0",
        "1,100,1",
        "25,100,25",
        "50,100,50",
        "100,100,100",
        "999,1000,99",
        "1000,1000,100",
    )
    fun getBatteryLevelPercentage_nonNullValidInput_returnsCorrectNonNullValue(level: Int?, scale: Int?, expected: Int?) {
        val result = BatteryStateRepositoryUtils.getBatteryLevelPercentage(level, scale)

        assertNotNull(result)
        assertEquals(expected, result)
    }

    @ParameterizedTest(name = "level={0}, scale={1} -> {2}%")
    @CsvSource(
        "-1, 0",
        "-1, 1",
        "0, -1",
        "0, 0",
        "1, 0",
        "2, 1",
        "-2, -3",
        " , -1",
        "-1, ",
        " , 0"
    )
    fun getBatteryLevelPercentage_invalidInput_returnsNull(level: Int?, scale: Int?) {
        assertNull(BatteryStateRepositoryUtils.getBatteryLevelPercentage(level, scale))
    }









    // getTemperatureInCelsius

    @Test
    fun getTemperatureInCelsius_nullInputs_returnsNull() {
        assertNull(BatteryStateRepositoryUtils.getTemperatureInCelsius(null))
    }


    @ParameterizedTest(name = "rawValue={0} -> temp={1}°C")
    @CsvSource(
        "-501, -50.1",
        "-500, -50.0",
        "-249, -24.9",
        "-100, -10.0",
        "-25, -2.5",
        "-1, -0.1",
        "-0, 0.0",
        "1, 0.1",
        "26, 2.6",
        "51, 5.1",
        "99, 9.9",
        "250, 25.0",
        "501, 50.1"
    )
    fun getTemperatureInCelsius_nonNullValidInput_returnsCorrectNonNullValue(rawValue: Int?, expected: Float?) {
        val result = BatteryStateRepositoryUtils.getTemperatureInCelsius(rawValue)

        assertNotNull(result)
        assertEquals(expected, result)
    }









    // getVoltage

    @Test
    fun getVoltage_nullInput_returnsNull() {
        assertNull(BatteryStateRepositoryUtils.getVoltage(null))
    }

    @ParameterizedTest(name = "rawValue={0} -> volts={1}V")
    @CsvSource(
        "-501, -0.501",
        "-500, -0.500",
        "-49, -0.049",
        "-25, -0.025",

        "-1, -0.001",
        "-0, 0.0",
        "1, 0.001",

        "26, 0.026",
        "51, 0.051",
        "75, 0.075",
        "99, 0.099",
        "250, 0.250",
        "501, 0.501"
    )
    fun getVoltage_validNonNUllInput_returnsCorrectNonNUllValue(rawValue: Int?, expected: Float?) {
        assertEquals(expected, BatteryStateRepositoryUtils.getVoltage(rawValue))
    }









    // getTechnology

    @ParameterizedTest(name = "rawString={0} -> technology={1}")
    @CsvSource(
        "Li-ion, Li-ion",
        "LiPo, LiPo",
        "Li-SOCl2, Li-SOCl2",
        "Li-MnO2, Li-MnO2",
        "NiMH, NiMH",
        "Button, Button"
    )
    fun getTechnology_validInput_returnsCorrectString(rawString: String?, expected: String) {
        assertEquals(BatteryStateRepositoryUtils.getTechnology(rawString), expected)
    }

    @Test
    fun getTechnology_nullInput_returnsNotAvailable() {
        assertEquals("Not available", BatteryStateRepositoryUtils.getTechnology(null))
    }






    // getHealth

    @Test
    fun getHealth_nullInput_returnsNotAvailable() {
        assertEquals(
            BatteryStateRepositoryUtils.getBatteryHealth(null),
            "Not available"
        )
    }

    @Test
    fun getHealth_batteryHealthCold_returnCold() {
        assertEquals(
            BatteryStateRepositoryUtils.getBatteryHealth(BatteryManager.BATTERY_HEALTH_COLD),
            "Cold"
        )
    }

    @Test
    fun getHealth_batteryHealthDead_returnDead() {
        assertEquals(
            BatteryStateRepositoryUtils.getBatteryHealth(BatteryManager.BATTERY_HEALTH_DEAD),
            "Dead"
        )
    }

    @Test
    fun getHealth_batteryHealthGood_returnGood() {
        assertEquals(
            BatteryStateRepositoryUtils.getBatteryHealth(BatteryManager.BATTERY_HEALTH_GOOD),
            "Good"
        )
    }

    @Test
    fun getHealth_batteryHealthOverheat_returnOverheated() {
        assertEquals(
            BatteryStateRepositoryUtils.getBatteryHealth(BatteryManager.BATTERY_HEALTH_OVERHEAT),
            "Overheated"
        )
    }

    @Test
    fun getHealth_batteryHealthOverVoltage_returnOvervoltage() {
        assertEquals(
            BatteryStateRepositoryUtils.getBatteryHealth(BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE),
            "Overvoltage"
        )
    }

    @Test
    fun getHealth_batteryHealthUnspecifiedFailure_returnFailure() {
        assertEquals(
            BatteryStateRepositoryUtils.getBatteryHealth(BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE),
            "Failure"
        )
    }

    @Test
    fun getHealth_batteryHealthUnknown_returnUnknown() {
        assertEquals(
            BatteryStateRepositoryUtils.getBatteryHealth(BatteryManager.BATTERY_HEALTH_UNKNOWN),
            "Unknown"
        )
    }








    // getChargingStatus

    @Test
    fun getChargingStatus_nullInput_returnsNotAvailable() {
        assertEquals(
            BatteryStateRepositoryUtils.getChargingStatus(null),
            "Not available"
        )
    }

    @Test
    fun getChargingStatus_statusFull_returnFull() {
        assertEquals(
            BatteryStateRepositoryUtils.getChargingStatus(BatteryManager.BATTERY_STATUS_FULL),
            "Full"
        )
    }

    @Test
    fun getChargingStatus_statusCharging_returnCharging() {
        assertEquals(
            BatteryStateRepositoryUtils.getChargingStatus(BatteryManager.BATTERY_STATUS_CHARGING),
            "Charging"
        )
    }

    @Test
    fun getChargingStatus_statusDischarging_returnDischarging() {
        assertEquals(
            BatteryStateRepositoryUtils.getChargingStatus(BatteryManager.BATTERY_STATUS_DISCHARGING),
            "Discharging"
        )
    }

    @Test
    fun getChargingStatus_statusNotCharging_returnNotCharging() {
        assertEquals(
            BatteryStateRepositoryUtils.getChargingStatus(BatteryManager.BATTERY_STATUS_NOT_CHARGING),
            "Not charging"
        )
    }

    @Test
    fun getChargingStatus_statusUnknown_returnUnknown() {
        assertEquals(
            BatteryStateRepositoryUtils.getChargingStatus(BatteryManager.BATTERY_STATUS_UNKNOWN),
            "Unknown"
        )
    }
}