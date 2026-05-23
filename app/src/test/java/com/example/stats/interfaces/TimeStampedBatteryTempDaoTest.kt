package com.example.stats.interfaces

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
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
import java.time.ZoneId
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class TimeStampedBatteryTempDaoTest {
    private lateinit var db: TimestampedBatteryTempDatabase
    private lateinit var dbDao: TimestampedBatteryTempDao

    val jan1st1970List = listOf(
        TimestampedBatteryTemp(timestamp = 150L, temperature = 39.2f),
        TimestampedBatteryTemp(timestamp = 1205L, temperature = -12.6f),
        TimestampedBatteryTemp(timestamp = 4567L, temperature = 36.8f),
        TimestampedBatteryTemp(timestamp = 7654L, temperature = -5.8f),
        TimestampedBatteryTemp(timestamp = 9876L, temperature = 27.5f),
        TimestampedBatteryTemp(timestamp = 11223L, temperature = 8.7f),
        TimestampedBatteryTemp(timestamp = 12345L, temperature = -9.2f),
        TimestampedBatteryTemp(timestamp = 21098L, temperature = 42.7f),
        TimestampedBatteryTemp(timestamp = 32156L, temperature = 15.2f),
        TimestampedBatteryTemp(timestamp = 33221L, temperature = -14.1f),
        TimestampedBatteryTemp(timestamp = 43210L, temperature = 23.4f),
        TimestampedBatteryTemp(timestamp = 44556L, temperature = -2.3f),
        TimestampedBatteryTemp(timestamp = 54321L, temperature = -18.4f),
        TimestampedBatteryTemp(timestamp = 55443L, temperature = 21.9f),
        TimestampedBatteryTemp(timestamp = 65432L, temperature = 33.1f),
        TimestampedBatteryTemp(timestamp = 66778L, temperature = 49.5f),
        TimestampedBatteryTemp(timestamp = 73210L, temperature = 25.6f),
        TimestampedBatteryTemp(timestamp = 77889L, temperature = 0.4f),
        TimestampedBatteryTemp(timestamp = 82100L, temperature = 44.3f),
        TimestampedBatteryTemp(timestamp = 84000L, temperature = 48.9f)
    )

    val jan2nd1970List = listOf(
        TimestampedBatteryTemp(timestamp = 88574L, temperature = 36.6f),
        TimestampedBatteryTemp(timestamp = 89825L, temperature = 1.3f),
        TimestampedBatteryTemp(timestamp = 95554L, temperature = 38.7f),
        TimestampedBatteryTemp(timestamp = 97682L, temperature = -4.7f),
        TimestampedBatteryTemp(timestamp = 99448L, temperature = -13.7f),
        TimestampedBatteryTemp(timestamp = 100157L, temperature = 1.1f),
        TimestampedBatteryTemp(timestamp = 100575L, temperature = 34.2f),
        TimestampedBatteryTemp(timestamp = 102639L, temperature = 32.0f),
        TimestampedBatteryTemp(timestamp = 112373L, temperature = 36.0f),
        TimestampedBatteryTemp(timestamp = 116194L, temperature = 30.7f),
        TimestampedBatteryTemp(timestamp = 119735L, temperature = 43.8f),
        TimestampedBatteryTemp(timestamp = 126105L, temperature = -19.4f),
        TimestampedBatteryTemp(timestamp = 130454L, temperature = -1.1f),
        TimestampedBatteryTemp(timestamp = 132696L, temperature = 42.9f),
        TimestampedBatteryTemp(timestamp = 138108L, temperature = 36.9f),
        TimestampedBatteryTemp(timestamp = 158066L, temperature = 38.0f),
        TimestampedBatteryTemp(timestamp = 158555L, temperature = 3.9f),
        TimestampedBatteryTemp(timestamp = 166817L, temperature = -4.4f)
    )

    val jan3rd1970List = listOf(
        TimestampedBatteryTemp(timestamp = 175113L, temperature = -6.8f),
        TimestampedBatteryTemp(timestamp = 187785L, temperature = -11.3f),
        TimestampedBatteryTemp(timestamp = 218870L, temperature = 17.4f),
        TimestampedBatteryTemp(timestamp = 219802L, temperature = 0.3f),
        TimestampedBatteryTemp(timestamp = 221579L, temperature = -15.2f),
        TimestampedBatteryTemp(timestamp = 221718L, temperature = 30.2f),
        TimestampedBatteryTemp(timestamp = 222786L, temperature = 19.4f),
        TimestampedBatteryTemp(timestamp = 224088L, temperature = -17.9f),
        TimestampedBatteryTemp(timestamp = 228233L, temperature = 12.5f),
        TimestampedBatteryTemp(timestamp = 228371L, temperature = -14.5f),
        TimestampedBatteryTemp(timestamp = 230554L, temperature = -19.8f),
        TimestampedBatteryTemp(timestamp = 231360L, temperature = 41.2f),
        TimestampedBatteryTemp(timestamp = 234072L, temperature = -2.1f),
        TimestampedBatteryTemp(timestamp = 237676L, temperature = -12.4f),
        TimestampedBatteryTemp(timestamp = 241590L, temperature = -7.7f),
        TimestampedBatteryTemp(timestamp = 242104L, temperature = -18.0f),
        TimestampedBatteryTemp(timestamp = 242117L, temperature = 1.5f),
        TimestampedBatteryTemp(timestamp = 254236L, temperature = 27.8f)
    )

    val jan4th1970List = listOf(
        TimestampedBatteryTemp(timestamp = 259465L, temperature = -11.7f),
        TimestampedBatteryTemp(timestamp = 261124L, temperature = 35.1f),
        TimestampedBatteryTemp(timestamp = 264760L, temperature = 8.7f),
        TimestampedBatteryTemp(timestamp = 272282L, temperature = 24.1f),
        TimestampedBatteryTemp(timestamp = 275623L, temperature = -17.7f),
        TimestampedBatteryTemp(timestamp = 275763L, temperature = 7.8f),
        TimestampedBatteryTemp(timestamp = 278931L, temperature = -7.4f),
        TimestampedBatteryTemp(timestamp = 280070L, temperature = 15.3f),
        TimestampedBatteryTemp(timestamp = 290562L, temperature = 41.0f),
        TimestampedBatteryTemp(timestamp = 292354L, temperature = -7.4f),
        TimestampedBatteryTemp(timestamp = 292761L, temperature = -10.0f),
        TimestampedBatteryTemp(timestamp = 296769L, temperature = 20.2f),
        TimestampedBatteryTemp(timestamp = 299315L, temperature = 19.5f),
        TimestampedBatteryTemp(timestamp = 299496L, temperature = -8.5f),
        TimestampedBatteryTemp(timestamp = 300073L, temperature = -18.2f),
        TimestampedBatteryTemp(timestamp = 304092L, temperature = -18.9f),
        TimestampedBatteryTemp(timestamp = 310526L, temperature = 0.7f),
        TimestampedBatteryTemp(timestamp = 321320L, temperature = -14.5f),
        TimestampedBatteryTemp(timestamp = 322356L, temperature = -13.2f),
        TimestampedBatteryTemp(timestamp = 326059L, temperature = 19.6f),
        TimestampedBatteryTemp(timestamp = 328386L, temperature = 18.8f),
        TimestampedBatteryTemp(timestamp = 328636L, temperature = 19.2f),
        TimestampedBatteryTemp(timestamp = 328796L, temperature = 28.8f),
        TimestampedBatteryTemp(timestamp = 333123L, temperature = 33.0f)
    )



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

        val resultList = getFirstValueOfFlow(
            dbDao.getAll()
        ).sortedBy { it -> it.timestamp }
        val expectedList = insertList.sortedBy { it -> it.timestamp }

        listAssertion(expectedList, resultList)
    }


    @Test
    fun getAll___validInputToPrefilledDatabase___correctOutput() {
        val prefilledList = jan1st1970List
        val newInsertList = jan2nd1970List + jan3rd1970List + jan4th1970List

        insertItems(dbDao, prefilledList)
        insertItems(dbDao, newInsertList)

        val resultList = getFirstValueOfFlow(
            dbDao.getAll()
        ).sortedBy { it -> it.timestamp }
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