package com.example.stats.interfaces

import com.example.stats.database.TimeStampedBatteryTemp
import com.example.stats.database.TimeStampedBatteryTempDao

interface BatteryTempHistoryRepositoryInterface {
    val className: String
    val dbDao: TimeStampedBatteryTempDao
    fun closeDB()

    suspend fun getBuffer(): List<TimeStampedBatteryTemp>
    suspend fun addItemToBuffer(item: TimeStampedBatteryTemp)
    suspend fun clearBuffer()
    suspend fun flushBuffer()
}