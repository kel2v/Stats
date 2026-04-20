package com.example.stats.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stats.utils.TimestampIntervalUtils
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId

@Dao
interface TimeStampedBatteryTempDao {
    @Query("SELECT * FROM timeStampedBatteryTemp")
    fun getAll(): Flow<List<TimeStampedBatteryTemp>>

    @Query("SELECT * FROM timeStampedBatteryTemp WHERE timestamp >= :startTimestamp AND timestamp <= :endTimestamp")
    fun getByTimestampRange(startTimestamp: Long, endTimestamp: Long): Flow<List<TimeStampedBatteryTemp>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(newInsertList: List<TimeStampedBatteryTemp>)

    fun getAllByDate(
        year: Int,
        month: Int,
        day: Int,
        zoneId: ZoneId
    ): Flow<List<TimeStampedBatteryTemp>> {
        val timestampBasedTimeInterval = TimestampIntervalUtils().convertDayIntoTimestampBasedInterval(year, month, day, zoneId)
        return getByTimestampRange(timestampBasedTimeInterval.startTimestamp, timestampBasedTimeInterval.endTimestamp)
    }
}