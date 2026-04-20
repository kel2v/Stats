package com.example.stats.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "timeStampedBatteryTemp",
    indices = [Index(value = ["timestamp"])]
)
data class TimeStampedBatteryTemp(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "temperature") val temperature: Float
)