package com.example.stats.interfaces

import com.example.stats.database.TimestampedBatteryTemp
import com.example.stats.database.TimestampedBatteryTempDao

interface BatteryTempHistoryRepositoryInterface {
    val className: String
    val dbDao: TimestampedBatteryTempDao
    fun closeDB()

    suspend fun getBuffer(): List<TimestampedBatteryTemp>
    suspend fun addItemToBuffer(item: TimestampedBatteryTemp)
    suspend fun clearBuffer()
    suspend fun flushBuffer()
}