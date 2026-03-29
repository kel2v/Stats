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
}