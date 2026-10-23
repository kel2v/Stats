package com.bytemanager.stats.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bytemanager.stats.data.types.TimestampedRawBatteryState

@Database(
    entities = [TimestampedRawBatteryState::class],
    version = 1,
    exportSchema = true
)
abstract class TimestampedBatteryStatesDatabase : RoomDatabase() {
    abstract fun timeStampedBatteryStatesDao(): TimestampedBatteryStatesDao
}