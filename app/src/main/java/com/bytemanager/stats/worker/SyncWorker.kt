package com.bytemanager.stats.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bytemanager.stats.interfaces.BatteryTempHistoryRepositoryInterface
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val batteryTempHistoryRepository: BatteryTempHistoryRepositoryInterface
): CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try {
            Log.d("DEBUGGING LOGS", "from SyncWorker: batteryTempHistoryRepository's class name = ${batteryTempHistoryRepository.className}")
            Log.d("DEBUGGING LOGS", "records count before sync = ${batteryTempHistoryRepository.dbDao.getAll().size}")
            batteryTempHistoryRepository.buffer.flushBuffer()
            Log.d("DEBUGGING LOGS", "records count after sync = ${batteryTempHistoryRepository.dbDao.getAll().size}")
            Result.success()
        } catch (e: Exception) {
            Log.d("DEBUGGING LOGS", "Caught exception: $e")

            if (runAttemptCount < 3) {
                Log.d("DEBUGGING LOGS", "retrying 'flushing buffer' ...")
                Result.retry()
            }
            else {
                Log.d("DEBUGGING LOGS", "'flushing buffer' failed")
                Result.failure()
            }
        }
    }
}