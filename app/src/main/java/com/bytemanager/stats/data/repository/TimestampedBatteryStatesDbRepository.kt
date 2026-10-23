package com.bytemanager.stats.data.repository

import android.content.Context
import androidx.room.Room
import com.bytemanager.stats.ApplicationCoroutineScope
import com.bytemanager.stats.data.database.TimestampedBatteryStatesDatabase
import com.bytemanager.stats.utils.BatteryStateRepositoryUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimestampedBatteryStatesDbRepository @Inject constructor(
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    @ApplicationContext private val appContext: Context
) {
    private val db = Room.databaseBuilder(
        appContext,
        TimestampedBatteryStatesDatabase::class.java,
        "com_bytemanager_stats.db"
    ).build()

    val dbDao = db.timeStampedBatteryStatesDao()

    val stateFlowOfTemperatureGlobalMin = dbDao.globalMinOfTemperature()
        .map{
            BatteryStateRepositoryUtils.getTemperatureInCelsius(it)
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val stateFlowOfTemperatureGlobalMax = dbDao.globalMaxOfTemperature()
        .map{
            BatteryStateRepositoryUtils.getTemperatureInCelsius(it)
        } .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val stateFlowOfVoltageGlobalMin = dbDao.globalMinOfVoltage()
        .map{
            BatteryStateRepositoryUtils.getVoltage(it)
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val stateFlowOfVoltageGlobalMax = dbDao.globalMaxOfVoltage()
        .map{
            BatteryStateRepositoryUtils.getVoltage(it)
        } .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}