package com.example.stats.interfaces

import com.example.stats.data.BatteryTempRecord

interface BatteryTempHistoryRepositoryInterface {
    fun getBatteryTempRecordOfDate(year: Int, month: Int, day: Int): List<BatteryTempRecord>
    fun updateBatteryTempDatabase(newDataList: List<BatteryTempRecord>): Boolean
}