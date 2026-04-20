package com.example.stats.repository

import android.content.Context
import androidx.room.Room
import com.example.stats.database.TimeStampedBatteryTempDao
import com.example.stats.database.TimestampedBatteryTempDatabase
import com.example.stats.interfaces.BatteryTempHistoryRepositoryInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BatteryTempHistoryRepository @Inject constructor(@ApplicationContext private val appContext: Context):
    BatteryTempHistoryRepositoryInterface {
    private val db = Room.databaseBuilder(
        appContext,
        TimestampedBatteryTempDatabase::class.java,
        "timeStampedBatteryTemp"
    ).build()

    override val dbDao: TimeStampedBatteryTempDao = db.timeStampedBatteryTempDao()
}