package com.example.stats.interfaces

import com.example.stats.database.TimestampedBatteryTemp
import com.example.stats.database.TimestampedBatteryTempDao
import com.example.stats.utils.TimestampedBatteryTempBuffer

interface BatteryTempHistoryRepositoryInterface {
    val className: String
    val dbDao: TimestampedBatteryTempDao
    fun closeDB()

    val buffer: TimestampedBatteryTempBuffer
}