package com.example.stats.repository

import android.content.Context
import androidx.room.Room
import com.example.stats.database.TimestampedBatteryTempDao
import com.example.stats.database.TimestampedBatteryTempDatabase
import com.example.stats.interfaces.BatteryTempHistoryRepositoryInterface
import com.example.stats.utils.TimestampedBatteryTempBuffer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BatteryTempHistoryRepository @Inject constructor(@ApplicationContext private val appContext: Context): BatteryTempHistoryRepositoryInterface {
    override val className = "BatteryTempHistoryRepository"
    private val db = Room.databaseBuilder(
        appContext,
        TimestampedBatteryTempDatabase::class.java,
        "timeStampedBatteryTemp"
    ).build()
    override val dbDao: TimestampedBatteryTempDao = db.timeStampedBatteryTempDao()

    override fun closeDB() {
        db.close()
    }


    override val buffer = TimestampedBatteryTempBuffer(dbDao)
}