package com.example.stats.interfaces

import com.example.stats.database.TimeStampedBatteryTempDao

interface BatteryTempHistoryRepositoryInterface {
    val dbDao: TimeStampedBatteryTempDao
}