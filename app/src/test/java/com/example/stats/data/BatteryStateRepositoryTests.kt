package com.example.stats.data

import com.example.stats.utils.BatteryStateRepositoryUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class BatteryStateRepositoryTests {
    @ParameterizedTest(name = "level={0}, scale={1} -> {2}%")
    @CsvSource(
        // theoretically enough values
        "0, 1, 0",
        "1, 2, 50",
        "2, 2, 100",

        // Arbitrary values
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
        "1,5,20",
        "2,5,40",
        "3,5,60",
        "4,5,80",
        "5,5,100",

        "0,10,0",
        "1,10,10",
        "2,10,20",
        "3,10,30",
        "4,10,40",
        "5,10,50",
        "6,10,60",
        "7,10,70",
        "8,10,80",
        "9,10,90",
        "10,10,100",

        "0,100,0",
        "1,100,1",
        "2,100,2",
        "25,100,25",
        "50,100,50",
        "75,100,75",
        "98,100,98",
        "99,100,99",
        "100,100,100",

        "0,1000,0",
        "1,1000,0",
        "2,1000,0",
        "25,1000,2",
        "50,1000,5",
        "75,1000,7",
        "100,1000,10",
        "125,1000,12",
        "250,1000,25",
        "375,1000,37",
        "500,1000,50",
        "625,1000,62",
        "750,1000,75",
        "875,1000,87",
        "998,1000,99",
        "999,1000,99",
        "1000,1000,100",
    )
    fun getBatteryLevelPercentage_validInput_returnsCorrect(level: Int, scale: Int, expected: Int) {
        assertEquals(BatteryStateRepositoryUtils().getBatteryLevelPercentage(level, scale), expected)
    }

    @ParameterizedTest(name = "level={0}, scale={1} -> {2}%")
    @CsvSource(
        // theoretically enough values
        "-2, -1, -1",
        "-1, -1, -1",
        "-1, -2, -1",
        "-1, 0, -1",
        "-1, 1, -1",
        "0, -1, -1",
        "0, 0, -1",
        "1, -1, -1",
        "1, 0, -1",
        "2, 1, -1",

        // Arbitrary values
        "-1000000, -1, -1",
        "-100, -1, -1",
        "-10, -1, -1",
        "-1, -1, -1",
        "0, -1, -1",
        "1, -1, -1",
        "10, -1, -1",
        "100, -1, -1",
        "1000000, -1, -1",

        "-1000000, 0, -1",
        "-100, 0, -1",
        "-10, 0, -1",
        "-1, 0, -1",
        "0, 0, -1",
        "1, 0, -1",
        "10, 0, -1",
        "100, 0, -1",
        "1000000, 0, -1",

        "-1000000, 1, -1",
        "-100, 1, -1",
        "-10, 1, -1",
        "-1, 1, -1",
        "2, 1, -1",
        "3, 1, -1",
        "10, 1, -1",
        "100, 1, -1",
        "1000000, 1, -1",

        "-1000000, 10, -1",
        "-100, 10, -1",
        "-10, 10, -1",
        "-1, 10, -1",
        "11, 10, -1",
        "12, 10, -1",
        "100, 10, -1",
        "1000000, 10, -1",
    )
    fun getBatteryLevelPercentage_invalidInput_returnsError(level: Int, scale: Int, expected: Int) {
        assertEquals(BatteryStateRepositoryUtils().getBatteryLevelPercentage(level, scale), expected)
    }


    @ParameterizedTest(name = "rawValue={0} -> temp={1}°C")
    @CsvSource(
        // Arbitrary values
        "-1000000, -100000.0",

        "-100000, -10000.0",

        "-10000, -1000.0",

        "-1002, -100.2",
        "-1001, -100.1",
        "-1000, -100.0",
        "-999, -99.9",
        "-998, -99.8",

        "-752, -75.2",
        "-751, -75.1",
        "-750, -75.0",
        "-749, -74.9",
        "-748, -74.8",

        "-502, -50.2",
        "-501, -50.1",
        "-500, -50.0",
        "-499, -49.9",
        "-498, -49.8",

        "-252, -25.2",
        "-251, -25.1",
        "-250, -25.0",
        "-249, -24.9",
        "-248, -24.8",

        "-102, -10.2",
        "-101, -10.1",
        "-100, -10.0",
        "-99, -9.9",
        "-98, -9.8",

        "-77, -7.7",
        "-76, -7.6",
        "-75, -7.5",
        "-74, -7.4",
        "-73, -7.3",

        "-52, -5.2",
        "-51, -5.1",
        "-50, -5.0",
        "-49, -4.9",
        "-48, -4.8",

        "-27, -2.7",
        "-26, -2.6",
        "-25, -2.5",
        "-24, -2.4",
        "-23, -2.3",

        "-2, -0.2",
        "-1, -0.1",
        "-0, 0.0",
        "1, 0.1",
        "2, 0.2",

        "27, 2.7",
        "26, 2.6",
        "25, 2.5",
        "24, 2.4",
        "23, 2.3",

        "52, 5.2",
        "51, 5.1",
        "50, 5.0",
        "49, 4.9",
        "48, 4.8",

        "77, 7.7",
        "76, 7.6",
        "75, 7.5",
        "74, 7.4",
        "73, 7.3",

        "102, 10.2",
        "101, 10.1",
        "100, 10.0",
        "99, 9.9",
        "98, 9.8",

        "252, 25.2",
        "251, 25.1",
        "250, 25.0",
        "249, 24.9",
        "248, 24.8",

        "502, 50.2",
        "501, 50.1",
        "500, 50.0",
        "499, 49.9",
        "498, 49.8",

        "752, 75.2",
        "751, 75.1",
        "750, 75.0",
        "749, 74.9",
        "748, 74.8",

        "1002, 100.2",
        "1001, 100.1",
        "1000, 100.0",
        "999, 99.9",
        "998, 99.8",

        "10000, 1000.0",

        "100000, 10000.0",

        "1000000, 100000.0"
    )
    fun getTemperatureInCelsius_validInput_correctOutput(rawValue: Int, expected: Float) {
        assertEquals(BatteryStateRepositoryUtils().getTemperatureInCelsius(rawValue), expected)
    }

    @ParameterizedTest(name = "rawValue={0} -> temp={1}°C")
    @CsvSource(
        "-2147483648, -3.4028235E38"
    )
    fun getTemperatureInCelsius_invalidInput_returnError(rawValue: Int, expected: Float) {
        assertEquals(BatteryStateRepositoryUtils().getTemperatureInCelsius(rawValue), expected)
    }
}