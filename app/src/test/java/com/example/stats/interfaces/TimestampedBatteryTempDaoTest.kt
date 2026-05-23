package com.example.stats.interfaces

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.stats.data.DbFillData
import com.example.stats.database.TimestampedBatteryTemp
import com.example.stats.database.TimestampedBatteryTempDao
import com.example.stats.database.TimestampedBatteryTempDatabase
import com.example.stats.utils.TimestampIntervalUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.ZoneId
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TimestampedBatteryTempDaoTest {
    private lateinit var db: TimestampedBatteryTempDatabase
    private lateinit var dbDao: TimestampedBatteryTempDao

    private val dbFillData = DbFillData()
    private val jan1st1970List = dbFillData.jan1st1970List
    private val jan2nd1970List = dbFillData.jan2nd1970List
    private val jan3rd1970List = dbFillData.jan3rd1970List
    private val jan4th1970List = dbFillData.jan4th1970List



    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TimestampedBatteryTempDatabase::class.java
        ).build()

        dbDao = db.timeStampedBatteryTempDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    fun insertItems(
        dao: TimestampedBatteryTempDao,
        inputList: List<TimestampedBatteryTemp>
    ) {
        runBlocking {
            dao.insertAll(inputList)
        }
    }

    fun getFirstValueOfFlow(listFlow: Flow<List<TimestampedBatteryTemp>>): List<TimestampedBatteryTemp> {
        var result: List<TimestampedBatteryTemp>
        runBlocking {
            result = listFlow.first()
        }

        return result
    }

    fun listAssertion(
        expectedList: List<TimestampedBatteryTemp>,
        resultList: List<TimestampedBatteryTemp>
    ) {
        val expectedListFinal = expectedList.sortedBy { it -> it.timestamp }
        val resultListFinal = resultList.sortedBy { it -> it.timestamp }

        assertEquals(expectedListFinal.size, resultListFinal.size)
        for(i in 0 .. expectedList.size - 1) {
            assertEquals(expectedListFinal[i].timestamp, resultListFinal[i].timestamp)
            assertEquals(expectedListFinal[i].temperature, resultListFinal[i].temperature)
        }
    }

    fun dayAssertion(
        dao: TimestampedBatteryTempDao,
        expectedListFull: List<TimestampedBatteryTemp>,
        year: Int, month: Int, day: Int,
        zoneId: ZoneId
    ) {
        val timestampRange = TimestampIntervalUtils().convertDayIntoTimestampBasedInterval(year, month, day, zoneId)
        val resultList = getFirstValueOfFlow(
            dao.getAllByDate(year, month, day, zoneId)
        )
        val expectedList = expectedListFull.filter {
            it.timestamp >= timestampRange.startTimestamp && it.timestamp <= timestampRange.endTimestamp
        }

        listAssertion(expectedList, resultList)
    }



    @Test
    fun getAll___validInputToFreshDatabase___correctOutput() {
        val insertList = jan1st1970List
        insertItems(dbDao, insertList)

        val resultList = runBlocking { dbDao.getAll() }.sortedBy { it -> it.timestamp }
        val expectedList = insertList.sortedBy { it -> it.timestamp }

        listAssertion(expectedList, resultList)
    }


    @Test
    fun getAll___validInputToPrefilledDatabase___correctOutput() {
        val prefilledList = jan1st1970List
        val newInsertList = jan2nd1970List + jan3rd1970List + jan4th1970List

        insertItems(dbDao, prefilledList)
        insertItems(dbDao, newInsertList)

        val resultList = runBlocking { dbDao.getAll() }.sortedBy { it -> it.timestamp }
        val expectedList = (prefilledList + newInsertList).sortedBy { it -> it.timestamp }

        listAssertion(expectedList, resultList)
    }


    @Test
    fun getAllByDate___validInputToFreshDatabase___correctOutput() {
        val insertList = jan1st1970List + jan2nd1970List + jan3rd1970List + jan4th1970List
        insertItems(dbDao, insertList)

        val expectedListFull = insertList

        dayAssertion(dbDao, expectedListFull, 1970, 1, 1, ZoneId.of("UTC"))
        dayAssertion(dbDao, expectedListFull, 1970, 1, 2, ZoneId.of("UTC"))
        dayAssertion(dbDao, expectedListFull, 1970, 1, 3, ZoneId.of("UTC"))
        dayAssertion(dbDao, expectedListFull, 1970, 1, 4, ZoneId.of("UTC"))
    }


    @Test
    fun getAllByDate___validInputToPrefilledDatabase___correctOutput() {
        val prefilledList = jan1st1970List
        val newInsertList = jan2nd1970List + jan3rd1970List + jan4th1970List
        insertItems(dbDao, prefilledList)
        insertItems(dbDao, newInsertList)

        val expectedListFull = prefilledList + newInsertList

        dayAssertion(dbDao, expectedListFull, 1970, 1, 1, ZoneId.of("UTC"))
        dayAssertion(dbDao, expectedListFull, 1970, 1, 2, ZoneId.of("UTC"))
        dayAssertion(dbDao, expectedListFull, 1970, 1, 3, ZoneId.of("UTC"))
        dayAssertion(dbDao, expectedListFull, 1970, 1, 4, ZoneId.of("UTC"))
    }

    @Test
    fun getAllByDate___nonAvailableValidDateQuery___nullListOutput() {
        val insertList = jan1st1970List + jan2nd1970List + jan3rd1970List + jan4th1970List
        insertItems(dbDao, insertList)

        val expectedListFull = insertList

        dayAssertion(dbDao, expectedListFull, 1970, 1, 15, ZoneId.of("UTC"))
        dayAssertion(dbDao, expectedListFull, 1970, 2, 1, ZoneId.of("UTC"))
        dayAssertion(dbDao, expectedListFull, 2000, 1, 1, ZoneId.of("UTC"))
        dayAssertion(dbDao, expectedListFull, 1910, 1, 1, ZoneId.of("UTC"))
    }


    @Test
    fun getAllByDate___invalidDateQuery___throwException() {
        val insertList = jan1st1970List + jan2nd1970List + jan3rd1970List + jan4th1970List
        insertItems(dbDao, insertList)

        val expectedListFull = insertList

        listOf(
            { dayAssertion(dbDao, expectedListFull, 1970, 1, 33, ZoneId.of("UTC")) },
            { dayAssertion(dbDao, expectedListFull, 1969, 2, 29, ZoneId.of("UTC")) },
            { dayAssertion(dbDao, expectedListFull, -1, -1, -1, ZoneId.of("UTC")) },
            { dayAssertion(dbDao, expectedListFull, 0, 0, 0, ZoneId.of("UTC")) }
        ).forEach {
            var isExceptionThrown = false
            try {
                it()
            } catch(_: Exception) {
                isExceptionThrown = true
            }
            assert(isExceptionThrown)
        }
    }
}