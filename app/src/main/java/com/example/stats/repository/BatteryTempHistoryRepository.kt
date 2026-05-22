package com.example.stats.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.stats.database.TimeStampedBatteryTemp
import com.example.stats.database.TimeStampedBatteryTempDao
import com.example.stats.database.TimestampedBatteryTempDatabase
import com.example.stats.hilt.ApplicationCoroutineScope
import com.example.stats.interfaces.BatteryTempHistoryRepositoryInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BatteryTempHistoryRepository @Inject constructor(@ApplicationContext private val appContext: Context, @ApplicationCoroutineScope private val appCoroutineScope: CoroutineScope, private val batteryStateRepository: BatteryStateRepository): BatteryTempHistoryRepositoryInterface {
    override val className = "BatteryTempHistoryRepository"
    private val db = Room.databaseBuilder(
        appContext,
        TimestampedBatteryTempDatabase::class.java,
        "timeStampedBatteryTemp"
    ).build()
    override val dbDao: TimeStampedBatteryTempDao = db.timeStampedBatteryTempDao()

    override fun closeDB() {
        db.close()
    }

    private val timestampedBatteryTempFlow = batteryStateRepository.batteryStateFlow.map {
        TimeStampedBatteryTemp(timestamp = it.timestamp, temperature = it.temperature)
    }




    private val bufferMutex = Mutex()
    private val buffer = mutableListOf<TimeStampedBatteryTemp>()

    init {
        Log.d("DEBUGGING LOGS", "Launching battery temp logger coroutine ...")
        appCoroutineScope.launch {
            Log.d("DEBUGGING LOGS", "battery temp logger coroutine launched!!")
            timestampedBatteryTempFlow.collect { value ->
                Log.d("DEBUGGING LOGS", "Adding new batteryTemp to buffer: $value")
                addItemToBuffer(value)
            }
        }
    }

    override suspend fun getBuffer(): List<TimeStampedBatteryTemp> {
        return bufferMutex.withLock { buffer.toList() }
    }

    override suspend fun addItemToBuffer(item: TimeStampedBatteryTemp) {
        bufferMutex.withLock { buffer.add(item) }
        Log.d("DEBUGGING LOGS", "buffer size after adding $item: ${buffer.size}")
    }

    override suspend fun clearBuffer() {
        bufferMutex.withLock { buffer.clear() }
        Log.d("DEBUGGING LOGS", "buffer size after clearing: ${buffer.size}")
    }

    override suspend fun flushBuffer() {
        bufferMutex.withLock {
            Log.d("DEBUGGING LOGS", "Flushing buffer")
            dbDao.insertAll(buffer.toList())
            Log.d("DEBUGGING LOGS", "After flushing, DB size = ${dbDao.getAll().first().size}")
            Log.d("DEBUGGING LOGS", "Clearing buffer")
            buffer.clear()
        }
    }
}