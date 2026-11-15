package com.bytemanager.stats.data.repository

import android.content.Context
import androidx.room.Room
import com.bytemanager.stats.data.database.TimestampedBatteryStatesDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimestampedBatteryStatesDbRepository @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    private val db = Room.databaseBuilder(
        appContext.createDeviceProtectedStorageContext(),
        TimestampedBatteryStatesDatabase::class.java,
        "com_bytemanager_stats.db"
    ).build()

    val dbDao = db.timeStampedBatteryStatesDao()
}