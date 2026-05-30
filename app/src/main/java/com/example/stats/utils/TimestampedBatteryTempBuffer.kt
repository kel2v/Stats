package com.example.stats.utils

import com.example.stats.database.TimestampedBatteryTemp
import com.example.stats.database.TimestampedBatteryTempDao
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TimestampedBatteryTempBuffer(private val dbDao: TimestampedBatteryTempDao) {
    private val bufferMutex = Mutex()
    private val buffer = mutableListOf<TimestampedBatteryTemp>()

    suspend fun getBuffer(): List<TimestampedBatteryTemp> {
        return bufferMutex.withLock { buffer.toList() }
    }

    suspend fun addItemToBuffer(item: TimestampedBatteryTemp) {
        bufferMutex.withLock {
            val alreadyExistingItem = buffer.find { it.timestamp == item.timestamp }
            if(alreadyExistingItem != null) {
                buffer.remove(alreadyExistingItem)
            }

            buffer.add(item)
        }
        //Log.d("DEBUGGING LOGS", "buffer size after adding $item: ${buffer.size}")
    }

    suspend fun clearBuffer() {
        bufferMutex.withLock { buffer.clear() }
        //Log.d("DEBUGGING LOGS", "buffer size after clearing: ${buffer.size}")
    }

    suspend fun flushBuffer() {
        bufferMutex.withLock {
            //Log.d("DEBUGGING LOGS", "Flushing buffer")
            dbDao.insertAll(buffer.toList())
            //Log.d("DEBUGGING LOGS", "After flushing, DB size = ${dbDao.getAll().size}")
            //Log.d("DEBUGGING LOGS", "Clearing buffer")
            buffer.clear()
        }
    }
}