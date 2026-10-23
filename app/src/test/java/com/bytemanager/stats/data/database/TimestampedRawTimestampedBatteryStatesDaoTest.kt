package com.bytemanager.stats.data.database

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.bytemanager.stats.data.types.TimestampedRawBatteryState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrowsExactly
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TimestampedRawTimestampedBatteryStatesDaoTest {
    companion object {
        private val allCombinationsOfParametersOfTimestampedRawBatteryStateLists = listOf(
            TimestampedRawBatteryState(timestamp = 1L, level = null, scale = null, voltage = null, temperature = null, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 2L, level = 50, scale = null, voltage = null, temperature = null, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 3L, level = null, scale = 100, voltage = null, temperature = null, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 4L, level = 50, scale = 100, voltage = null, temperature = null, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 5L, level = null, scale = null, voltage = 4200, temperature = null, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 6L, level = 50, scale = null, voltage = 4200, temperature = null, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 7L, level = null, scale = 100, voltage = 4200, temperature = null, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 8L, level = 50, scale = 100, voltage = 4200, temperature = null, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 9L, level = null, scale = null, voltage = null, temperature = 35, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 10L, level = 50, scale = null, voltage = null, temperature = 35, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 11L, level = null, scale = 100, voltage = null, temperature = 35, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 12L, level = 50, scale = 100, voltage = null, temperature = 35, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 13L, level = null, scale = null, voltage = 4200, temperature = 35, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 14L, level = 50, scale = null, voltage = 4200, temperature = 35, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 15L, level = null, scale = 100, voltage = 4200, temperature = 35, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 16L, level = 50, scale = 100, voltage = 4200, temperature = 35, chargingStatus = null, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 17L, level = null, scale = null, voltage = null, temperature = null, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 18L, level = 50, scale = null, voltage = null, temperature = null, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 19L, level = null, scale = 100, voltage = null, temperature = null, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 20L, level = 50, scale = 100, voltage = null, temperature = null, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 21L, level = null, scale = null, voltage = 4200, temperature = null, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 22L, level = 50, scale = null, voltage = 4200, temperature = null, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 23L, level = null, scale = 100, voltage = 4200, temperature = null, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 24L, level = 50, scale = 100, voltage = 4200, temperature = null, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 25L, level = null, scale = null, voltage = null, temperature = 35, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 26L, level = 50, scale = null, voltage = null, temperature = 35, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 27L, level = null, scale = 100, voltage = null, temperature = 35, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 28L, level = 50, scale = 100, voltage = null, temperature = 35, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 29L, level = null, scale = null, voltage = 4200, temperature = 35, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 30L, level = 50, scale = null, voltage = 4200, temperature = 35, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 31L, level = null, scale = 100, voltage = 4200, temperature = 35, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 32L, level = 50, scale = 100, voltage = 4200, temperature = 35, chargingStatus = 2, batteryHealth = null),
            TimestampedRawBatteryState(timestamp = 33L, level = null, scale = null, voltage = null, temperature = null, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 34L, level = 50, scale = null, voltage = null, temperature = null, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 35L, level = null, scale = 100, voltage = null, temperature = null, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 36L, level = 50, scale = 100, voltage = null, temperature = null, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 37L, level = null, scale = null, voltage = 4200, temperature = null, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 38L, level = 50, scale = null, voltage = 4200, temperature = null, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 39L, level = null, scale = 100, voltage = 4200, temperature = null, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 40L, level = 50, scale = 100, voltage = 4200, temperature = null, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 41L, level = null, scale = null, voltage = null, temperature = 35, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 42L, level = 50, scale = null, voltage = null, temperature = 35, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 43L, level = null, scale = 100, voltage = null, temperature = 35, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 44L, level = 50, scale = 100, voltage = null, temperature = 35, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 45L, level = null, scale = null, voltage = 4200, temperature = 35, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 46L, level = 50, scale = null, voltage = 4200, temperature = 35, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 47L, level = null, scale = 100, voltage = 4200, temperature = 35, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 48L, level = 50, scale = 100, voltage = 4200, temperature = 35, chargingStatus = null, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 49L, level = null, scale = null, voltage = null, temperature = null, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 50L, level = 50, scale = null, voltage = null, temperature = null, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 51L, level = null, scale = 100, voltage = null, temperature = null, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 52L, level = 50, scale = 100, voltage = null, temperature = null, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 53L, level = null, scale = null, voltage = 4200, temperature = null, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 54L, level = 50, scale = null, voltage = 4200, temperature = null, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 55L, level = null, scale = 100, voltage = 4200, temperature = null, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 56L, level = 50, scale = 100, voltage = 4200, temperature = null, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 57L, level = null, scale = null, voltage = null, temperature = 35, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 58L, level = 50, scale = null, voltage = null, temperature = 35, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 59L, level = null, scale = 100, voltage = null, temperature = 35, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 60L, level = 50, scale = 100, voltage = null, temperature = 35, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 61L, level = null, scale = null, voltage = 4200, temperature = 35, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 62L, level = 50, scale = null, voltage = 4200, temperature = 35, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 63L, level = null, scale = 100, voltage = 4200, temperature = 35, chargingStatus = 2, batteryHealth = 2),
            TimestampedRawBatteryState(timestamp = 64L, level = 50, scale = 100, voltage = 4200, temperature = 35, chargingStatus = 2, batteryHealth = 2),
        )



        private val prefillList = listOf(
            TimestampedRawBatteryState(101),
            TimestampedRawBatteryState(102, temperature = 10),
            TimestampedRawBatteryState(103, temperature = 9),
            TimestampedRawBatteryState(104, temperature = 5),
            TimestampedRawBatteryState(105),
            TimestampedRawBatteryState(106, temperature = -20),
            TimestampedRawBatteryState(107),
            TimestampedRawBatteryState(108, temperature = -50),
            TimestampedRawBatteryState(109, temperature = 0)
        )
        private val distinctTimestampNullParametersList = listOf(
            TimestampedRawBatteryState(201),
            TimestampedRawBatteryState(202),
            TimestampedRawBatteryState(203),
            TimestampedRawBatteryState(204),
            TimestampedRawBatteryState(205),
            TimestampedRawBatteryState(206),
        )

        private val repeatingTimestampNullParametersList = listOf(
            TimestampedRawBatteryState(301),
            TimestampedRawBatteryState(301),
            TimestampedRawBatteryState(302),
            TimestampedRawBatteryState(303),
            TimestampedRawBatteryState(303),
            TimestampedRawBatteryState(304),
            TimestampedRawBatteryState(305),
        )

        private val distinctTimestampNonNullParametersList = listOf(
            TimestampedRawBatteryState(401, 0, 0, 0, 0, 0, 0),
            TimestampedRawBatteryState(402, 2, 2, 2, 2, 2, 2),
            TimestampedRawBatteryState(403, 3, 3, 3, 3, 3, 3),
            TimestampedRawBatteryState(404, 4, 4, 4, 4, 4, 4),
            TimestampedRawBatteryState(405, 5, 5, 5, 5, 5, 5),
            TimestampedRawBatteryState(406, 6, 6, 6, 6, 6, 6),
            TimestampedRawBatteryState(407, 0, 0, 0, -100, 0, 0),
            TimestampedRawBatteryState(408, 0, 0, 0, 0, 0, 0),
            TimestampedRawBatteryState(409, 0, 0, 0, 0, 0, 0)
        )
    }

    private lateinit var db: TimestampedBatteryStatesDatabase
    private lateinit var dbDao: TimestampedBatteryStatesDao
    private lateinit var appContext: Context


    @Before
    fun setup() {
        appContext  = ApplicationProvider.getApplicationContext()

        db = Room.inMemoryDatabaseBuilder(
            appContext,
            TimestampedBatteryStatesDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dbDao = db.timeStampedBatteryStatesDao()
    }

    @After
    fun tearDown() {
        db.close()
    }


    private suspend fun prefillDatabase(dbDao: TimestampedBatteryStatesDao, prefillList: List<TimestampedRawBatteryState>) {
        assertDoesNotThrow {
            dbDao.insertListOfItemsWithAbort(prefillList)
        }
    }

    private fun listAssertion(
        expectedList: List<TimestampedRawBatteryState>,
        resultList: List<TimestampedRawBatteryState>
    ) {
        val expectedListFinal = expectedList.sortedBy { it.timestamp }
        val resultListFinal = resultList.sortedBy { it.timestamp }

        assertEquals(expectedListFinal, resultListFinal)
    }






    // insertItemWithAbort
    @Test
    fun insertItemWithAbort_emptyDatabase_noExceptionsThrown() = runTest {
        allCombinationsOfParametersOfTimestampedRawBatteryStateLists.forEach {
            val insertItem = it
            assertDoesNotThrow {
                dbDao.insertItemWithAbort(insertItem)
            }
        }
    }

    @Test
    fun insertItemWithAbort_insertDistinctItemToNonEmptyDatabase_noExceptionsThrown() = runTest {
        prefillDatabase(dbDao, prefillList)
        allCombinationsOfParametersOfTimestampedRawBatteryStateLists.forEach {
            val insertItem = it
            assertDoesNotThrow {
                dbDao.insertItemWithAbort(insertItem)
            }
        }
    }

    @Test
    fun insertItemWithAbort_insertRepeatingItemToNonEmptyDatabase_throwsSQLiteConstraintException() = runTest {
        prefillDatabase(dbDao, prefillList)
        val insertItem = prefillList[0]
        assertThrowsExactly<SQLiteConstraintException> {
            dbDao.insertItemWithAbort(insertItem)
        }
    }





    // insertListOfItemsWithAbort
    @Test
    fun insertListOfItemsWithAbort_insertListOfDistinctItemsToEmptyDatabase_noExceptionsThrown() = runTest {
        val insertList = allCombinationsOfParametersOfTimestampedRawBatteryStateLists
        assertDoesNotThrow {
            dbDao.insertListOfItemsWithAbort(insertList)
        }
    }

    @Test
    fun insertListOfItemsWithAbort_insertListOfRepeatingItemsToEmptyDatabase_throwsSQLiteConstraintException() = runTest {
        val insertList = repeatingTimestampNullParametersList
        assertThrowsExactly<SQLiteConstraintException> {
            dbDao.insertListOfItemsWithAbort(insertList)
        }
    }


    @Test
    fun insertListOfItemsWithAbort_insertListOfDistinctItemsToNonEmptyDatabaseWithNoMatchingItemToInsertListItems_noExceptionsThrown() = runTest {
        prefillDatabase(dbDao, prefillList)

        val insertList = allCombinationsOfParametersOfTimestampedRawBatteryStateLists
        assertDoesNotThrow {
            dbDao.insertListOfItemsWithAbort(insertList)
        }
    }

    @Test
    fun insertListOfItemsWithAbort_insertListOfDistinctItemsToNonEmptyDatabaseWithAtLeastOneItemMatchingAInsertListItem_throwsSQLiteConstraintException() = runTest {
        prefillDatabase(dbDao, prefillList)

        val insertList = allCombinationsOfParametersOfTimestampedRawBatteryStateLists + prefillList[0]
        assertThrowsExactly<SQLiteConstraintException> {
            dbDao.insertListOfItemsWithAbort(insertList)
        }
    }


    @Test
    fun insertListOfItemsWithAbort_insertListOfRepeatingItemsToNonEmptyDatabaseWithNoItemMatchingAInsertListItem_throwsSQLiteConstraintException() = runTest {
        prefillDatabase(dbDao, prefillList)

        val insertList = repeatingTimestampNullParametersList
        assertThrowsExactly<SQLiteConstraintException> {
            dbDao.insertListOfItemsWithAbort(insertList)
        }
    }

    @Test
    fun insertListOfItemsWithAbort_insertListOfRepeatingItemsToNonEmptyDatabaseWithAtLeastOneMatchingItemToInsertListItems_throwsSQLiteConstraintException() = runTest {
        prefillDatabase(dbDao, prefillList)

        val insertList = repeatingTimestampNullParametersList + prefillList[0]
        assertThrowsExactly<SQLiteConstraintException> {
            dbDao.insertListOfItemsWithAbort(insertList)
        }
    }






    // deleteItem
    @Test
    fun deleteItem_deleteItemFromEmptyDatabase_returnsZero() = runTest {
        val deleteItem = distinctTimestampNullParametersList[0]
        assertEquals(0, dbDao.deleteItem(deleteItem))
    }

    @Test
    fun deleteItem_deleteItemThatExistsInNonEmptyDatabase_returnsOne() = runTest {
        prefillDatabase(dbDao, prefillList)

        val deleteItem = prefillList[0]
        assertEquals(1, dbDao.deleteItem(deleteItem))
    }

    @Test
    fun deleteItem_deleteItemThatDoNotExistsInNonEmptyDatabase_returnsZero() = runTest {
        prefillDatabase(dbDao, prefillList)

        val deleteItem = distinctTimestampNullParametersList[0]
        assertEquals(0, dbDao.deleteItem(deleteItem))
    }





    // deleteListOfItems

    @Test
    fun deleteListOfItems_deleteListOfDistinctItemsFromEmptyDatabase_returnsZero() = runTest {
        val listOfDeleteItems = distinctTimestampNullParametersList
        assertEquals(0, dbDao.deleteListOfItems(listOfDeleteItems))
    }

    @Test
    fun deleteListOfItems_deleteListOfRepeatedItemsFromEmptyDatabase_returnsZero() = runTest {
        val listOfDeleteItems = repeatingTimestampNullParametersList
        assertEquals(0, dbDao.deleteListOfItems(listOfDeleteItems))
    }

    @Test
    fun deleteListOfItems_deleteListOfDistinctItemsNonOfWhichExistsInNonEmptyDatabase_returnZero() = runTest {
        prefillDatabase(dbDao, prefillList)

        val listOfDeleteItems = distinctTimestampNullParametersList
        assertEquals(0, dbDao.deleteListOfItems(listOfDeleteItems))
    }

    @Test
    fun deleteListOfItems_deleteListOfDistinctItemsSomeOfWhichExistsInNonEmptyDatabase_returnNumberOfItemsInDeleteListThatExistedInDatabase() = runTest {
        prefillDatabase(dbDao, prefillList)

        val listOfDeleteItems = distinctTimestampNullParametersList + prefillList.slice(0 until prefillList.size/2)
        assertEquals(prefillList.size/2, dbDao.deleteListOfItems(listOfDeleteItems))
    }

    @Test
    fun deleteListOfItems_deleteListOfDistinctItemsAllOfWhichExistsInNonEmptyDatabase_returnDeleteListSize() = runTest {
        prefillDatabase(dbDao, prefillList)

        val listOfDeleteItems = prefillList.slice(0 until prefillList.size/2)
        assertEquals(prefillList.size/2, dbDao.deleteListOfItems(listOfDeleteItems))
    }

    @Test
    fun deleteListOfItems_deleteListOfRepeatedItemsNoneOfWhichExistsInNonEmptyDatabase_returnZero() = runTest {
        prefillDatabase(dbDao, prefillList)

        val listOfDeleteItems = repeatingTimestampNullParametersList
        assertEquals(0, dbDao.deleteListOfItems(listOfDeleteItems))
    }

    @Test
    fun deleteListOfItems_deleteListOfRepeatedItemsSomeOfWhichExistsInNonEmptyDatabase_returnNumberOfUniqueItemsInDeleteListThatExistedInDatabase() = runTest {
        prefillDatabase(dbDao, prefillList)

        val listOfDeleteItems = repeatingTimestampNullParametersList + prefillList.slice(0 until prefillList.size/2)
        assertEquals(prefillList.size/2, dbDao.deleteListOfItems(listOfDeleteItems))
    }

    @Test
    fun deleteListOfItems_deleteListOfRepeatedItemsAllOfWhichExistsInNonEmptyDatabase_returnNumberOfUniqueItemsInDeleteList() = runTest {
        prefillDatabase(dbDao, distinctTimestampNullParametersList)

        val listOfDeleteItems = distinctTimestampNullParametersList + distinctTimestampNullParametersList.map { TimestampedRawBatteryState(it.timestamp, 0, 0, 0, 0, 0, 0) }
        assertEquals(distinctTimestampNullParametersList.size, dbDao.deleteListOfItems(listOfDeleteItems))
    }













    // getCount
    @Test
    fun getCount_emptyDatabase_zeroAsOutput() = runTest {
        assertEquals(0, dbDao.getCount().first())
    }

    @Test
    fun getCount_nonEmptyDatabase_correctOutput() = runTest {
        prefillDatabase(dbDao, prefillList)
        assertEquals(prefillList.size, dbDao.getCount().first())
    }







    // getAll
    @Test
    fun getAll_emptyDatabase_emptyListAsOutput() = runTest {
        assertTrue(dbDao.getAll().first().isEmpty())
    }


    @Test
    fun getAll_nonEmptyDatabase_correctOutput() = runTest {
        prefillDatabase(dbDao, prefillList)
        listAssertion(prefillList, dbDao.getAll().first())
    }






    // getFromTimestampRange
    @Test
    fun getFromTimestampRange_validRangeEmptyDatabase_emptyListAsOutput() = runTest {
        assertTrue(dbDao.getFromTimestampRange(0, 1000).first().isEmpty())
    }

    @Test
    fun getFromTimestampRange_invalidRangeEmptyDatabase_emptyListAsOutput() = runTest {
        assertTrue(dbDao.getFromTimestampRange(1000, 0).first().isEmpty())
    }

    @Test
    fun getFromTimestampRange_sameStartAndEndTimestamp_EmptyDatabase_emptyListAsOutput() = runTest {
        assertTrue(dbDao.getFromTimestampRange(1, 1).first().isEmpty())
    }

    @Test
    fun getFromTimestampRange_startTimestampIsInclusive_NonEmptyDatabase_resultListContainsItemWithStartTimestamp() = runTest {
        prefillDatabase(dbDao, prefillList)
        val randomItem = prefillList.random()
        val resultList = dbDao.getFromTimestampRange(randomItem.timestamp, randomItem.timestamp + 1000).first()
        assertNotNull(resultList.find { it.timestamp == randomItem.timestamp })
    }

    @Test
    fun getFromTimestampRange_endTimestampIsExclusive_NonEmptyDatabase_resultListNotContainsItemWithEndTimestamp() = runTest {
        prefillDatabase(dbDao, prefillList)
        val randomItem = prefillList.random()
        val resultList = dbDao.getFromTimestampRange(randomItem.timestamp - 1000, randomItem.timestamp).first()
        assertNull(resultList.find { it.timestamp == randomItem.timestamp })
    }

    @Test
    fun getFromTimestampRange_sameStartAndEndTimestampEqualingToAnItemTimestamp_NonEmptyDatabase_emptyListASResult() = runTest {
        prefillDatabase(dbDao, prefillList)
        val randomItem = prefillList.random()
        val resultList = dbDao.getFromTimestampRange(randomItem.timestamp, randomItem.timestamp).first()
        assertTrue(resultList.isEmpty())
    }

    @Test
    fun getFromTimestampRange_tryGettingSingleItemWithItsTimestampAndItsIncrementValue_NonEmptyDatabase_SingletonListWithSubjectItemReturned() = runTest {
        prefillDatabase(dbDao, prefillList)
        val randomItem = prefillList.random()
        val resultList = dbDao.getFromTimestampRange(randomItem.timestamp, randomItem.timestamp+1).first()
        listAssertion(listOf(randomItem), resultList)
    }

    @Test
    fun getFromTimestampRange_validRangeWithNoMatchingItems_NonEmptyDatabase_emptyListAsOutput() = runTest {
        prefillDatabase(dbDao, prefillList)

        val maxTimestamp = prefillList.maxOf { it.timestamp }
        val startTimestamp = maxTimestamp + 1
        val endTimestamp = startTimestamp + 1000

        assertTrue(dbDao.getFromTimestampRange(startTimestamp, endTimestamp).first().isEmpty())
    }

    @Test
    fun getFromTimestampRange_validRangeWithMatchingItems_NonEmptyDatabase_correctOutput() = runTest {
        prefillDatabase(dbDao, prefillList)

        val sortedPrefillList = prefillList.sortedBy { it.timestamp }
        val randomItem = sortedPrefillList.random()
        val startTimestamp = randomItem.timestamp
        val endTimestamp = startTimestamp + 1000
        val resultList = dbDao.getFromTimestampRange(startTimestamp, endTimestamp).first()
        val expectedList = sortedPrefillList.filter { it.timestamp in startTimestamp until endTimestamp }

        listAssertion(expectedList, resultList)
    }




    // minTemperature
    @Test
    fun globalMin_OfTemperature_emptyDatabase_nullAsOutput() = runTest {
        assertNull(dbDao.globalMinOfTemperature().firstOrNull())
    }

    @Test
    fun globalMin_OfTemperature_onlyNullTemperaturesInNonEmptyDatabase_nullAsOutput() = runTest {
        prefillDatabase(dbDao, distinctTimestampNullParametersList)
        assertNull(dbDao.globalMinOfTemperature().firstOrNull())
    }

    @Test
    fun globalMinOfTemperature() = runTest {
        prefillDatabase(dbDao, (distinctTimestampNullParametersList + distinctTimestampNonNullParametersList))
        val expectedMinValue = distinctTimestampNonNullParametersList.minBy { it.temperature!! }.temperature
        val resultValue = dbDao.globalMinOfTemperature().firstOrNull()

        assertNotNull(resultValue)
        assertEquals(expectedMinValue, resultValue)
    }

    @Test
    fun globalMin_OfTemperature_onlyNonNullTemperaturesInNonEmptyDatabase_correctOutput() = runTest {
        prefillDatabase(dbDao, distinctTimestampNonNullParametersList)

        val expectedMinValue = distinctTimestampNonNullParametersList.minBy { it.temperature!! }.temperature
        val resultValue = dbDao.globalMinOfTemperature().firstOrNull()

        assertNotNull(resultValue)
        assertEquals(expectedMinValue, resultValue)
    }




    // maxTemperature
    @Test
    fun globalMax_OfTemperature_emptyDatabase_nullAsOutput() = runTest {
        assertNull(dbDao.globalMaxOfTemperature().firstOrNull())
    }

    @Test
    fun globalMax_OfTemperature_onlyNullTemperaturesInNonEmptyDatabase_nullAsOutput() = runTest {
        prefillDatabase(dbDao, distinctTimestampNullParametersList)
        assertNull(dbDao.globalMaxOfTemperature().firstOrNull())
    }

    @Test
    fun globalMax_OfTemperature_bothNullAndNonNullTemperaturesInNonEmptyDatabase_correctOutput() = runTest {
        prefillDatabase(dbDao, (distinctTimestampNullParametersList + distinctTimestampNonNullParametersList))

        val expectedMaxValue = distinctTimestampNonNullParametersList.maxBy{ it.temperature!! }.temperature
        val resultValue = dbDao.globalMaxOfTemperature().firstOrNull()

        assertNotNull(resultValue)
        assertEquals(expectedMaxValue, resultValue)
    }

    @Test
    fun globalMax_OfTemperature_onlyNonNullTemperaturesInNonEmptyDatabase_correctOutput() = runTest {
        prefillDatabase(dbDao, distinctTimestampNonNullParametersList)

        val expectedMaxValue = distinctTimestampNonNullParametersList.maxBy{ it.temperature!! }.temperature
        val resultValue = dbDao.globalMaxOfTemperature().firstOrNull()

        assertNotNull(resultValue)
        assertEquals(expectedMaxValue, resultValue)
    }





    // replaceAllItems

    @Test
    fun replaceAllItems_replaceListWithDistinctItems_emptyDatabase_finishesNormally() = runTest {
        assertDoesNotThrow {
            dbDao.replaceAllItems(distinctTimestampNullParametersList)
        }
    }

    @Test
    fun replaceAllItems_replaceListWithRepeatedItems_emptyDatabase_throwsSQLiteConstraintException() = runTest {
        assertThrowsExactly<SQLiteConstraintException> {
            dbDao.replaceAllItems(repeatingTimestampNullParametersList)
        }
    }

    @Test
    fun replaceAllItems_replaceListWithDistinctItems_nonEmptyDatabase_throwsSQLiteConstraintException() = runTest {
        prefillDatabase(dbDao, prefillList)
        assertDoesNotThrow {
            dbDao.replaceAllItems(distinctTimestampNullParametersList)
        }
    }

    @Test
    fun replaceAllItems_replaceListWithRepeatedItems_nonEmptyDatabase_throwsSQLiteConstraintException() = runTest {
        prefillDatabase(dbDao, prefillList)
        assertThrowsExactly<SQLiteConstraintException> {
            dbDao.replaceAllItems(repeatingTimestampNullParametersList)
        }
    }
}