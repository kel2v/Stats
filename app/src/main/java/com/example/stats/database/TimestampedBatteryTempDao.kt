package com.example.stats.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stats.utils.TimestampIntervalUtils
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId

@Dao
interface TimestampedBatteryTempDao {
    @Query("SELECT * FROM timestampedBatteryTemp")
    suspend fun getAll(): List<TimestampedBatteryTemp>

    @Query("SELECT * FROM timestampedBatteryTemp WHERE timestamp >= :startTimestamp AND timestamp <= :endTimestamp")
    fun getByTimestampRange(startTimestamp: Long, endTimestamp: Long): Flow<List<TimestampedBatteryTemp>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(newInsertList: List<TimestampedBatteryTemp>)

    @Query("DELETE FROM timestampedBatteryTemp")
    suspend fun deleteAll()

    fun getAllByDate(
        year: Int,
        month: Int,
        day: Int,
        zoneId: ZoneId
    ): Flow<List<TimestampedBatteryTemp>> {
        val timestampBasedTimeInterval = TimestampIntervalUtils().convertDayIntoTimestampBasedInterval(year, month, day, zoneId)
        return getByTimestampRange(timestampBasedTimeInterval.startTimestamp, timestampBasedTimeInterval.endTimestamp)
    }
}