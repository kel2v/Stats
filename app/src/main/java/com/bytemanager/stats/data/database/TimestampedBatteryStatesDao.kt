package com.bytemanager.stats.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bytemanager.stats.data.types.TimestampedRawBatteryState
import kotlinx.coroutines.flow.Flow

@Dao
interface TimestampedBatteryStatesDao {
    // ABORT - Throws an exception on conflict
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertItemWithAbort(insertItem: TimestampedRawBatteryState)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemWithReplace(insertItem: TimestampedRawBatteryState)


    // ABORT - Throws exception if any row conflicts
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertListOfItemsWithAbort(insertList: List<TimestampedRawBatteryState>)




    // Deletion
    @Delete
    suspend fun deleteItem(deleteItem: TimestampedRawBatteryState): Int

    @Delete
    suspend fun deleteListOfItems(deleteList: List<TimestampedRawBatteryState>): Int

    @Query("DELETE FROM timestampedBatteryStates")
    suspend fun deleteAll(): Int





    // Retrieval
    @Query("SELECT COUNT(*) FROM timestampedBatteryStates")
    fun getCount(): Flow<Int>


    @Query("SELECT * FROM timestampedBatteryStates WHERE timestamp = :timestamp")
    fun getItem(timestamp: Long): Flow<TimestampedRawBatteryState?>

    @Query("SELECT * FROM timestampedBatteryStates")
    fun getAll(): Flow<List<TimestampedRawBatteryState>>

    @Query("SELECT * FROM timestampedBatteryStates WHERE timestamp >= :startTimestamp and timestamp < :endTimestamp")
    fun getFromTimestampRange(startTimestamp: Long, endTimestamp: Long): Flow<List<TimestampedRawBatteryState>>




    @Query("SELECT MIN(voltage) FROM timestampedBatteryStates")
    fun globalMinOfVoltage(): Flow<Int?>

    @Query("SELECT MAX(voltage) FROM timestampedBatteryStates")
    fun globalMaxOfVoltage(): Flow<Int?>


    @Query("SELECT MIN(temperature) FROM timestampedBatteryStates")
    fun globalMinOfTemperature(): Flow<Int?>

    @Query("SELECT MAX(temperature) FROM timestampedBatteryStates")
    fun globalMaxOfTemperature(): Flow<Int?>





    // Modification
    @Transaction
    suspend fun replaceAllItems(replaceList: List<TimestampedRawBatteryState>) {
        deleteAll()
        insertListOfItemsWithAbort(replaceList)
    }
}