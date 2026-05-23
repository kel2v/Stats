package com.example.stats.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.stats.interfaces.BatteryTempHistoryRepositoryInterface
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class SyncWorker @AssistedInject constructor(@Assisted private val appContext: Context, @Assisted private val params: WorkerParameters, private val batteryTempHistoryRepository: BatteryTempHistoryRepositoryInterface): CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        Log.d("DEBUGGING LOGS", "from SyncWorker: batteryTempHistoryRepository's class name = ${batteryTempHistoryRepository.className}")
        return try {
            Log.d("DEBUGGING LOGS", "'battery temp logging' started")
            Log.d("DEBUGGING LOGS", "records count before sync = ${batteryTempHistoryRepository.dbDao.getAll().size}")
            batteryTempHistoryRepository.flushBuffer()
            Log.d("DEBUGGING LOGS", "records count after sync = ${batteryTempHistoryRepository.dbDao.getAll().size}")
            Log.d("DEBUGGING LOGS", "'battery temp logging' completed")
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Log.d("DEBUGGING LOGS", "retrying 'battery temp logging' ...")
                Result.retry()
            }
            else {
                Log.d("DEBUGGING LOGS", "'battery temp logging' failed")
                Result.failure()
            }
        }
    }
}