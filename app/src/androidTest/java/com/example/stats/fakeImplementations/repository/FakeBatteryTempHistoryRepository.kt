package com.example.stats.fakeImplementations.repository

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.stats.database.TimeStampedBatteryTemp
import com.example.stats.database.TimestampedBatteryTempDatabase
import com.example.stats.interfaces.BatteryTempHistoryRepositoryInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeBatteryTempHistoryRepository @Inject constructor(@ApplicationContext private val appContext: Context): BatteryTempHistoryRepositoryInterface {
    override val className = "FakeBatteryTempHistoryRepository"
    private val db = Room.inMemoryDatabaseBuilder(appContext, TimestampedBatteryTempDatabase::class.java).build()
    override val dbDao = db.timeStampedBatteryTempDao()

    override fun closeDB() {
        db.close()
    }


    private val bufferMutex = Mutex()
    private val buffer = mutableListOf<TimeStampedBatteryTemp>()

    override suspend fun getBuffer(): List<TimeStampedBatteryTemp> {
        return bufferMutex.withLock { buffer.toList() }
    }

    override suspend fun addItemToBuffer(item: TimeStampedBatteryTemp) {
        bufferMutex.withLock { buffer.add(item) }
    }

    override suspend fun clearBuffer() {
        bufferMutex.withLock { buffer.clear() }
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