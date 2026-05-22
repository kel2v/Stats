package com.example.stats.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TimeStampedBatteryTemp::class],
    version = 1,
    exportSchema = true
)
abstract class TimestampedBatteryTempDatabase : RoomDatabase() {
    abstract fun timeStampedBatteryTempDao(): TimeStampedBatteryTempDao
}