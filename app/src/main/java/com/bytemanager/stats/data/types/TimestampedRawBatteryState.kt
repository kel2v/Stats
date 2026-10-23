package com.bytemanager.stats.data.types

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "timestampedBatteryStates",
)
data class TimestampedRawBatteryState(
    @PrimaryKey val timestamp: Long,
    @ColumnInfo(name = "level") val level: Int? = null,
    @ColumnInfo(name = "scale") val scale: Int? = null,
    @ColumnInfo(name = "voltage") val voltage: Int? = null,
    @ColumnInfo(name = "temperature") val temperature: Int? = null,
    @ColumnInfo(name = "chargingStatus") val chargingStatus: Int? = null,
    @ColumnInfo(name = "batteryHealth") val batteryHealth: Int? = null,
)