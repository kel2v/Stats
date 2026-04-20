package com.example.stats.interfaces

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.stats.database.TimeStampedBatteryTemp
import com.example.stats.database.TimeStampedBatteryTempDao
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
    private lateinit var dbDao: TimeStampedBatteryTempDao

    val jan1st1970List = listOf(
        TimeStampedBatteryTemp(timestamp = 150L, temperature = 39.2f),
        TimeStampedBatteryTemp(timestamp = 1205L, temperature = -12.6f),
        TimeStampedBatteryTemp(timestamp = 4567L, temperature = 36.8f),
        TimeStampedBatteryTemp(timestamp = 7654L, temperature = -5.8f),
        TimeStampedBatteryTemp(timestamp = 9876L, temperature = 27.5f),
        TimeStampedBatteryTemp(timestamp = 11223L, temperature = 8.7f),
        TimeStampedBatteryTemp(timestamp = 12345L, temperature = -9.2f),
        TimeStampedBatteryTemp(timestamp = 21098L, temperature = 42.7f),
        TimeStampedBatteryTemp(timestamp = 32156L, temperature = 15.2f),
        TimeStampedBatteryTemp(timestamp = 33221L, temperature = -14.1f),
        TimeStampedBatteryTemp(timestamp = 43210L, temperature = 23.4f),
        TimeStampedBatteryTemp(timestamp = 44556L, temperature = -2.3f),
        TimeStampedBatteryTemp(timestamp = 54321L, temperature = -18.4f),
        TimeStampedBatteryTemp(timestamp = 55443L, temperature = 21.9f),
        TimeStampedBatteryTemp(timestamp = 65432L, temperature = 33.1f),
        TimeStampedBatteryTemp(timestamp = 66778L, temperature = 49.5f),
        TimeStampedBatteryTemp(timestamp = 73210L, temperature = 25.6f),
        TimeStampedBatteryTemp(timestamp = 77889L, temperature = 0.4f),
        TimeStampedBatteryTemp(timestamp = 82100L, temperature = 44.3f),
        TimeStampedBatteryTemp(timestamp = 84000L, temperature = 48.9f)
    )

    val jan2nd1970List = listOf(
        TimeStampedBatteryTemp(timestamp = 88574L, temperature = 36.6f),
        TimeStampedBatteryTemp(timestamp = 89825L, temperature = 1.3f),
        TimeStampedBatteryTemp(timestamp = 95554L, temperature = 38.7f),
        TimeStampedBatteryTemp(timestamp = 97682L, temperature = -4.7f),
        TimeStampedBatteryTemp(timestamp = 99448L, temperature = -13.7f),
        TimeStampedBatteryTemp(timestamp = 100157L, temperature = 1.1f),
        TimeStampedBatteryTemp(timestamp = 100575L, temperature = 34.2f),
        TimeStampedBatteryTemp(timestamp = 102639L, temperature = 32.0f),
        TimeStampedBatteryTemp(timestamp = 112373L, temperature = 36.0f),
        TimeStampedBatteryTemp(timestamp = 116194L, temperature = 30.7f),
        TimeStampedBatteryTemp(timestamp = 119735L, temperature = 43.8f),
        TimeStampedBatteryTemp(timestamp = 126105L, temperature = -19.4f),
        TimeStampedBatteryTemp(timestamp = 130454L, temperature = -1.1f),
        TimeStampedBatteryTemp(timestamp = 132696L, temperature = 42.9f),
        TimeStampedBatteryTemp(timestamp = 138108L, temperature = 36.9f),
        TimeStampedBatteryTemp(timestamp = 158066L, temperature = 38.0f),
        TimeStampedBatteryTemp(timestamp = 158555L, temperature = 3.9f),
        TimeStampedBatteryTemp(timestamp = 166817L, temperature = -4.4f)
    )

    val jan3rd1970List = listOf(
        TimeStampedBatteryTemp(timestamp = 175113L, temperature = -6.8f),
        TimeStampedBatteryTemp(timestamp = 187785L, temperature = -11.3f),
        TimeStampedBatteryTemp(timestamp = 218870L, temperature = 17.4f),
        TimeStampedBatteryTemp(timestamp = 219802L, temperature = 0.3f),
        TimeStampedBatteryTemp(timestamp = 221579L, temperature = -15.2f),
        TimeStampedBatteryTemp(timestamp = 221718L, temperature = 30.2f),
        TimeStampedBatteryTemp(timestamp = 222786L, temperature = 19.4f),
        TimeStampedBatteryTemp(timestamp = 224088L, temperature = -17.9f),
        TimeStampedBatteryTemp(timestamp = 228233L, temperature = 12.5f),
        TimeStampedBatteryTemp(timestamp = 228371L, temperature = -14.5f),
        TimeStampedBatteryTemp(timestamp = 230554L, temperature = -19.8f),
        TimeStampedBatteryTemp(timestamp = 231360L, temperature = 41.2f),
        TimeStampedBatteryTemp(timestamp = 234072L, temperature = -2.1f),
        TimeStampedBatteryTemp(timestamp = 237676L, temperature = -12.4f),
        TimeStampedBatteryTemp(timestamp = 241590L, temperature = -7.7f),
        TimeStampedBatteryTemp(timestamp = 242104L, temperature = -18.0f),
        TimeStampedBatteryTemp(timestamp = 242117L, temperature = 1.5f),
        TimeStampedBatteryTemp(timestamp = 254236L, temperature = 27.8f)
    )

    val jan4th1970List = listOf(
        TimeStampedBatteryTemp(timestamp = 259465L, temperature = -11.7f),
        TimeStampedBatteryTemp(timestamp = 261124L, temperature = 35.1f),
        TimeStampedBatteryTemp(timestamp = 264760L, temperature = 8.7f),
        TimeStampedBatteryTemp(timestamp = 272282L, temperature = 24.1f),
        TimeStampedBatteryTemp(timestamp = 275623L, temperature = -17.7f),
        TimeStampedBatteryTemp(timestamp = 275763L, temperature = 7.8f),
        TimeStampedBatteryTemp(timestamp = 278931L, temperature = -7.4f),
        TimeStampedBatteryTemp(timestamp = 280070L, temperature = 15.3f),
        TimeStampedBatteryTemp(timestamp = 290562L, temperature = 41.0f),
        TimeStampedBatteryTemp(timestamp = 292354L, temperature = -7.4f),
        TimeStampedBatteryTemp(timestamp = 292761L, temperature = -10.0f),
        TimeStampedBatteryTemp(timestamp = 296769L, temperature = 20.2f),
        TimeStampedBatteryTemp(timestamp = 299315L, temperature = 19.5f),
        TimeStampedBatteryTemp(timestamp = 299496L, temperature = -8.5f),
        TimeStampedBatteryTemp(timestamp = 300073L, temperature = -18.2f),
        TimeStampedBatteryTemp(timestamp = 304092L, temperature = -18.9f),
        TimeStampedBatteryTemp(timestamp = 310526L, temperature = 0.7f),
        TimeStampedBatteryTemp(timestamp = 321320L, temperature = -14.5f),
        TimeStampedBatteryTemp(timestamp = 322356L, temperature = -13.2f),
        TimeStampedBatteryTemp(timestamp = 326059L, temperature = 19.6f),
        TimeStampedBatteryTemp(timestamp = 328386L, temperature = 18.8f),
        TimeStampedBatteryTemp(timestamp = 328636L, temperature = 19.2f),
        TimeStampedBatteryTemp(timestamp = 328796L, temperature = 28.8f),
        TimeStampedBatteryTemp(timestamp = 333123L, temperature = 33.0f)
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
        dao: TimeStampedBatteryTempDao,
        inputList: List<TimeStampedBatteryTemp>
    ) {
        runBlocking {
            dao.insertAll(inputList)
        }
    }

    fun getFirstValueOfFlow(listFlow: Flow<List<TimeStampedBatteryTemp>>): List<TimeStampedBatteryTemp> {
        var result: List<TimeStampedBatteryTemp>
        runBlocking {
            result = listFlow.first()
        }

        return result
    }

    fun listAssertion(
        expectedList: List<TimeStampedBatteryTemp>,
        resultList: List<TimeStampedBatteryTemp>
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
        dao: TimeStampedBatteryTempDao,
        expectedListFull: List<TimeStampedBatteryTemp>,
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