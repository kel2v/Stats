package com.bytemanager.stats.utils

import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import com.bytemanager.stats.data.types.TimestampInterval
import org.junit.Test
import org.junit.jupiter.params.provider.Arguments
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.ZoneId
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StatsTimeUtilsTest {
    companion object {
        val zoneIdOfUTC: ZoneId? = ZoneId.of("UTC")
        val zoneIdOfAsiaKolkata: ZoneId? = ZoneId.of("Asia/Kolkata")
        val zoneIdOfAmericaNewYork: ZoneId? = ZoneId.of("America/New_York")
        val zoneIdOfAsiaTokyo: ZoneId? = ZoneId.of("Asia/Tokyo")
        val zoneIdOfEuropeMoscow: ZoneId? = ZoneId.of("Europe/Moscow")

        const val americaNewYorkDSTOffset = 14400L
        const val americaNewYorkOffset = 18000L
        const val asiaKolkataOffset = -19800L
        const val asiaTokyoOffset = -32400L
        const val europeMoscowOffset = -10800L

        @JvmStatic
        fun millisToLocalDateSourceProvider(): List<Arguments> {
            if(zoneIdOfUTC == null || zoneIdOfAmericaNewYork == null || zoneIdOfAsiaKolkata == null || zoneIdOfAsiaTokyo == null || zoneIdOfEuropeMoscow == null) {
                throw(NullPointerException())
            }

            return listOf(
                Arguments.of(null, zoneIdOfUTC, LocalDate.of(1970, 1, 1)),
                Arguments.of(0L, zoneIdOfUTC, LocalDate.of(1970, 1, 1)),
                Arguments.of(1777075200000L, zoneIdOfUTC, LocalDate.of(2026, 4, 25)),
                Arguments.of(2147483647000L, zoneIdOfUTC, LocalDate.of(2038, 1, 19)),
                Arguments.of(2147483648000L, zoneIdOfUTC, LocalDate.of(2038, 1, 19)),

                Arguments.of(null, zoneIdOfAsiaKolkata, LocalDate.of(1970, 1, 1)),
                Arguments.of(0L + asiaKolkataOffset, zoneIdOfAsiaKolkata, LocalDate.of(1970, 1, 1)),
                Arguments.of(1777075200000L + asiaKolkataOffset, zoneIdOfAsiaKolkata, LocalDate.of(2026, 4, 25)),
                Arguments.of(2147483647000L + asiaKolkataOffset, zoneIdOfAsiaKolkata, LocalDate.of(2038, 1, 19)),
                Arguments.of(2147483648000L + asiaKolkataOffset, zoneIdOfAsiaKolkata, LocalDate.of(2038, 1, 19)),

                Arguments.of(null, zoneIdOfAsiaTokyo, LocalDate.of(1970, 1, 1)),
                Arguments.of(0 + asiaTokyoOffset, zoneIdOfAsiaTokyo, LocalDate.of(1970, 1, 1)),
                Arguments.of(1777075200000L + asiaTokyoOffset, zoneIdOfAsiaTokyo, LocalDate.of(2026, 4, 25)),
                Arguments.of(2147483647000L + asiaTokyoOffset, zoneIdOfAsiaTokyo, LocalDate.of(2038, 1, 19)),
                Arguments.of(2147483648000L + asiaTokyoOffset, zoneIdOfAsiaTokyo, LocalDate.of(2038, 1, 19)),

                Arguments.of(null, zoneIdOfEuropeMoscow, LocalDate.of(1970, 1, 1)),
                Arguments.of(0L + europeMoscowOffset, zoneIdOfEuropeMoscow, LocalDate.of(1970, 1, 1)),
                Arguments.of(1777075200000L + europeMoscowOffset, zoneIdOfEuropeMoscow, LocalDate.of(2026, 4, 25)),
                Arguments.of(2147483647000L + europeMoscowOffset, zoneIdOfEuropeMoscow, LocalDate.of(2038, 1, 19)),
                Arguments.of(2147483648000L + europeMoscowOffset, zoneIdOfEuropeMoscow, LocalDate.of(2038, 1, 19))
            )
        }

        @JvmStatic
        fun convertDayIntoTimestampBasedIntervalSourceProvider(): List<Arguments> {
            return listOf(
                Arguments.of(1970, 1, 1, zoneIdOfUTC, TimestampInterval(0L, 86399L)),
                Arguments.of(2020, 2, 29, zoneIdOfUTC, TimestampInterval(1582934400L, 1583020799L)),
                Arguments.of(2038, 1, 18, zoneIdOfUTC, TimestampInterval(2147385600L, 2147471999L)),
                Arguments.of(2038, 1, 19, zoneIdOfUTC, TimestampInterval(2147472000L, 2147558399L)),
                Arguments.of(2038, 1, 20, zoneIdOfUTC, TimestampInterval(2147558400L, 2147644799L)),

                Arguments.of(1970, 1, 1, zoneIdOfAsiaKolkata, TimestampInterval(0L + asiaKolkataOffset, 86399L + asiaKolkataOffset)),
                Arguments.of(2020, 2, 29, zoneIdOfAsiaKolkata, TimestampInterval(1582934400L + asiaKolkataOffset, 1583020799L + asiaKolkataOffset)),
                Arguments.of(2038, 1, 18, zoneIdOfAsiaKolkata, TimestampInterval(2147385600L + asiaKolkataOffset, 2147471999L + asiaKolkataOffset)),
                Arguments.of(2038, 1, 19, zoneIdOfAsiaKolkata, TimestampInterval(2147472000L + asiaKolkataOffset, 2147558399L + asiaKolkataOffset)),
                Arguments.of(2038, 1, 20, zoneIdOfAsiaKolkata, TimestampInterval(2147558400L + asiaKolkataOffset, 2147644799L + asiaKolkataOffset)),

                Arguments.of(1970, 1, 1, zoneIdOfAsiaTokyo, TimestampInterval(0L + asiaTokyoOffset, 86399L + asiaTokyoOffset)),
                Arguments.of(2020, 2, 29, zoneIdOfAsiaTokyo, TimestampInterval(1582934400L + asiaTokyoOffset, 1583020799L + asiaTokyoOffset)),
                Arguments.of(2038, 1, 18, zoneIdOfAsiaTokyo, TimestampInterval(2147385600L + asiaTokyoOffset, 2147471999L + asiaTokyoOffset)),
                Arguments.of(2038, 1, 19, zoneIdOfAsiaTokyo, TimestampInterval(2147472000L + asiaTokyoOffset, 2147558399L + asiaTokyoOffset)),
                Arguments.of(2038, 1, 20, zoneIdOfAsiaTokyo, TimestampInterval(2147558400L + asiaTokyoOffset, 2147644799L + asiaTokyoOffset)),

                Arguments.of(1970, 1, 1, zoneIdOfEuropeMoscow, TimestampInterval(0L + europeMoscowOffset, 86399L + europeMoscowOffset)),
                Arguments.of(2020, 2, 29, zoneIdOfEuropeMoscow,
                    TimestampInterval(
                        1582934400L + europeMoscowOffset,
                        1583020799L + europeMoscowOffset
                    )
                ),
                Arguments.of(2038, 1, 18, zoneIdOfEuropeMoscow, TimestampInterval(2147385600L + europeMoscowOffset, 2147471999L + europeMoscowOffset)),
                Arguments.of(2038, 1, 19, zoneIdOfEuropeMoscow, TimestampInterval(2147472000L + europeMoscowOffset, 2147558399L + europeMoscowOffset)),
                Arguments.of(2038, 1, 20, zoneIdOfEuropeMoscow, TimestampInterval(2147558400L + europeMoscowOffset, 2147644799L + europeMoscowOffset)),
            )
        }
    }

    @Test
    fun epochMillisToLocalDate_onMillisBeingNull_returnsNull() {
        assertNull(StatsTimeUtils().epochMillisToLocalDate(null))
    }


    private data class EpochMillisLocalDatePair(
        val epochMillis: Long,
        val correspondingLocalDate: LocalDate,
        val zoneId: ZoneId,
    )

    @Test
    fun epochMillisToLocalDate_validNonNullValuesOfMillisParameter_returnsCorrectly() {

        val pairsByZoneId = mapOf(
            zoneIdOfAmericaNewYork!! to listOf(
                EpochMillisLocalDatePair(1704067200000L, LocalDate.of(2023, 12, 31), zoneIdOfAmericaNewYork),
                EpochMillisLocalDatePair(1710977400000L, LocalDate.of(2024, 3, 20), zoneIdOfAmericaNewYork),
                EpochMillisLocalDatePair(1718452800000L, LocalDate.of(2024, 6, 15), zoneIdOfAmericaNewYork),
                EpochMillisLocalDatePair(1725156000000L, LocalDate.of(2024, 8, 31), zoneIdOfAmericaNewYork),
                EpochMillisLocalDatePair(1735687800000L, LocalDate.of(2024, 12, 31), zoneIdOfAmericaNewYork),
            ),
            zoneIdOfAsiaKolkata!! to listOf(
                EpochMillisLocalDatePair(1704067200000L, LocalDate.of(2024, 1, 1), zoneIdOfAsiaKolkata),
                EpochMillisLocalDatePair(1710977400000L, LocalDate.of(2024, 3, 21), zoneIdOfAsiaKolkata),
                EpochMillisLocalDatePair(1718452800000L, LocalDate.of(2024, 6, 15), zoneIdOfAsiaKolkata),
                EpochMillisLocalDatePair(1725156000000L, LocalDate.of(2024, 9, 1), zoneIdOfAsiaKolkata),
                EpochMillisLocalDatePair(1735687800000L, LocalDate.of(2025, 1, 1), zoneIdOfAsiaKolkata),
            ),
            zoneIdOfAsiaTokyo!! to listOf(
                EpochMillisLocalDatePair(1704067200000L, LocalDate.of(2024, 1, 1), zoneIdOfAsiaTokyo),
                EpochMillisLocalDatePair(1710977400000L, LocalDate.of(2024, 3, 21), zoneIdOfAsiaTokyo),
                EpochMillisLocalDatePair(1718452800000L, LocalDate.of(2024, 6, 15), zoneIdOfAsiaTokyo),
                EpochMillisLocalDatePair(1725156000000L, LocalDate.of(2024, 9, 1), zoneIdOfAsiaTokyo),
                EpochMillisLocalDatePair(1735687800000L, LocalDate.of(2025, 1, 1), zoneIdOfAsiaTokyo),
            ),
            zoneIdOfEuropeMoscow!! to listOf(
                EpochMillisLocalDatePair(1704067200000L, LocalDate.of(2024, 1, 1), zoneIdOfEuropeMoscow),
                EpochMillisLocalDatePair(1710977400000L, LocalDate.of(2024, 3, 21), zoneIdOfEuropeMoscow),
                EpochMillisLocalDatePair(1718452800000L, LocalDate.of(2024, 6, 15), zoneIdOfEuropeMoscow),
                EpochMillisLocalDatePair(1725156000000L, LocalDate.of(2024, 9, 1), zoneIdOfEuropeMoscow),
                EpochMillisLocalDatePair(1735687800000L, LocalDate.of(2025, 1, 1), zoneIdOfEuropeMoscow),
            ),
            zoneIdOfUTC!! to listOf(
                EpochMillisLocalDatePair(1704067200000L, LocalDate.of(2024, 1, 1), zoneIdOfUTC),
                EpochMillisLocalDatePair(1710977400000L, LocalDate.of(2024, 3, 20), zoneIdOfUTC),
                EpochMillisLocalDatePair(1718452800000L, LocalDate.of(2024, 6, 15), zoneIdOfUTC),
                EpochMillisLocalDatePair(1725156000000L, LocalDate.of(2024, 9, 1), zoneIdOfUTC),
                EpochMillisLocalDatePair(1735687800000L, LocalDate.of(2024, 12, 31), zoneIdOfUTC),
            ),
        )

        pairsByZoneId.forEach { (zoneId, pairs) ->
            val utils = StatsTimeUtils(Clock.system(zoneId))

            pairs.forEach { pair ->
                assertEquals(pair.correspondingLocalDate, utils.epochMillisToLocalDate(pair.epochMillis))
            }
        }
    }

    private data class EpochSecondsOfStartOfTheDayLocalDatePair(
        val epochSecondOfStartOfTheDay: Long,
        val correspondingLocalDate: LocalDate,
        val zoneId: ZoneId
    )
    @Test
    fun localDateToStartOfTheDayInEpochSeconds_validInput_correctOutput() {
        val pairsByZoneId = mapOf(
            zoneIdOfAmericaNewYork!! to listOf(
                EpochSecondsOfStartOfTheDayLocalDatePair(1703998800L, LocalDate.of(2023, 12, 31), zoneIdOfAmericaNewYork),
                EpochSecondsOfStartOfTheDayLocalDatePair(1718424000L, LocalDate.of(2024, 6, 15), zoneIdOfAmericaNewYork),
                EpochSecondsOfStartOfTheDayLocalDatePair(1710907200L, LocalDate.of(2024, 3, 20), zoneIdOfAmericaNewYork),
                EpochSecondsOfStartOfTheDayLocalDatePair(1725076800L, LocalDate.of(2024, 8, 31), zoneIdOfAmericaNewYork),
                EpochSecondsOfStartOfTheDayLocalDatePair(1735621200L, LocalDate.of(2024, 12, 31), zoneIdOfAmericaNewYork),
            ),
            zoneIdOfAsiaKolkata!! to listOf(
                EpochSecondsOfStartOfTheDayLocalDatePair(1704047400L, LocalDate.of(2024, 1, 1), zoneIdOfAsiaKolkata),
                EpochSecondsOfStartOfTheDayLocalDatePair(1710959400L, LocalDate.of(2024, 3, 21), zoneIdOfAsiaKolkata),
                EpochSecondsOfStartOfTheDayLocalDatePair(1718389800L, LocalDate.of(2024, 6, 15), zoneIdOfAsiaKolkata),
                EpochSecondsOfStartOfTheDayLocalDatePair(1725129000L, LocalDate.of(2024, 9, 1), zoneIdOfAsiaKolkata),
                EpochSecondsOfStartOfTheDayLocalDatePair(1735669800L, LocalDate.of(2025, 1, 1), zoneIdOfAsiaKolkata),
            ),
            zoneIdOfAsiaTokyo!! to listOf(
                EpochSecondsOfStartOfTheDayLocalDatePair(1704034800L, LocalDate.of(2024, 1, 1), zoneIdOfAsiaTokyo),
                EpochSecondsOfStartOfTheDayLocalDatePair(1710946800L, LocalDate.of(2024, 3, 21), zoneIdOfAsiaTokyo),
                EpochSecondsOfStartOfTheDayLocalDatePair(1718377200L, LocalDate.of(2024, 6, 15), zoneIdOfAsiaTokyo),
                EpochSecondsOfStartOfTheDayLocalDatePair(1725116400L, LocalDate.of(2024, 9, 1), zoneIdOfAsiaTokyo),
                EpochSecondsOfStartOfTheDayLocalDatePair(1735657200L, LocalDate.of(2025, 1, 1), zoneIdOfAsiaTokyo),
            ),
            zoneIdOfEuropeMoscow!! to listOf(
                EpochSecondsOfStartOfTheDayLocalDatePair(1704056400L, LocalDate.of(2024, 1, 1), zoneIdOfEuropeMoscow),
                EpochSecondsOfStartOfTheDayLocalDatePair(1710968400L, LocalDate.of(2024, 3, 21), zoneIdOfEuropeMoscow),
                EpochSecondsOfStartOfTheDayLocalDatePair(1718398800L, LocalDate.of(2024, 6, 15), zoneIdOfEuropeMoscow),
                EpochSecondsOfStartOfTheDayLocalDatePair(1725138000L, LocalDate.of(2024, 9, 1), zoneIdOfEuropeMoscow),
                EpochSecondsOfStartOfTheDayLocalDatePair(1735678800L, LocalDate.of(2025, 1, 1), zoneIdOfEuropeMoscow),
            ),
            zoneIdOfUTC!! to listOf(
                EpochSecondsOfStartOfTheDayLocalDatePair(1704067200L, LocalDate.of(2024, 1, 1), zoneIdOfUTC),
                EpochSecondsOfStartOfTheDayLocalDatePair(1710892800L, LocalDate.of(2024, 3, 20), zoneIdOfUTC),
                EpochSecondsOfStartOfTheDayLocalDatePair(1718409600L, LocalDate.of(2024, 6, 15), zoneIdOfUTC),
                EpochSecondsOfStartOfTheDayLocalDatePair(1725148800L, LocalDate.of(2024, 9, 1), zoneIdOfUTC),
                EpochSecondsOfStartOfTheDayLocalDatePair(1735603200L, LocalDate.of(2024, 12, 31), zoneIdOfUTC),
            ),
        )


        pairsByZoneId.forEach { (zoneId, pairs) ->
            val utils = StatsTimeUtils(Clock.system(zoneId))

            pairs.forEach { pair ->
                assertEquals(pair.epochSecondOfStartOfTheDay, utils.localDateToStartOfTheDayInEpochSeconds(pair.correspondingLocalDate))
            }
        }
    }


    private data class LocalDateTimestampIntervalPair(
        val localDate: LocalDate,
        val startTimestamp: Long,
        val endTimestamp: Long,
    )
    @Test
    fun localDateToTimestampInterval_validInput_correctOutput() {
        val pairsByZoneId = mapOf(
            zoneIdOfAmericaNewYork!! to listOf(
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 2, 29), 1709182800L, 1709269200L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 3, 10), 1710046800L, 1710129600L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 11, 3), 1730606400L, 1730696400L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 12, 31), 1735621200L, 1735707600L),
                LocalDateTimestampIntervalPair(LocalDate.of(2025, 1, 1), 1735707600L, 1735794000L),
            ),
            zoneIdOfAsiaKolkata!! to listOf(
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 2, 29), 1709145000L, 1709231400L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 3, 10), 1710009000L, 1710095400L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 11, 3), 1730572200L, 1730658600L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 12, 31), 1735583400L, 1735669800L),
                LocalDateTimestampIntervalPair(LocalDate.of(2025, 1, 1), 1735669800L, 1735756200L),
            ),
            zoneIdOfAsiaTokyo!! to listOf(
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 2, 29), 1709132400L, 1709218800L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 3, 10), 1709996400L, 1710082800L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 11, 3), 1730559600L, 1730646000L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 12, 31), 1735570800L, 1735657200L),
                LocalDateTimestampIntervalPair(LocalDate.of(2025, 1, 1), 1735657200L, 1735743600L),
            ),
            zoneIdOfEuropeMoscow!! to listOf(
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 2, 29), 1709154000L, 1709240400L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 3, 10), 1710018000L, 1710104400L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 11, 3), 1730581200L, 1730667600L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 12, 31), 1735592400L, 1735678800L),
                LocalDateTimestampIntervalPair(LocalDate.of(2025, 1, 1), 1735678800L, 1735765200L),
            ),
            zoneIdOfUTC!! to listOf(
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 2, 29), 1709164800L, 1709251200L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 3, 10), 1710028800L, 1710115200L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 11, 3), 1730592000L, 1730678400L),
                LocalDateTimestampIntervalPair(LocalDate.of(2024, 12, 31), 1735603200L, 1735689600L),
                LocalDateTimestampIntervalPair(LocalDate.of(2025, 1, 1), 1735689600L, 1735776000L),
            ),
        )

        pairsByZoneId.forEach { (zoneId, pairs) ->
            val clock = Clock.system(zoneId)
            val utils = StatsTimeUtils(clock)

            pairs.forEach { pair ->
                assertEquals(
                    TimestampInterval(pair.startTimestamp, pair.endTimestamp),
                    utils.localDateToTimestampInterval(pair.localDate)
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun dateRangeToTimestampInterval_bothDatesAreNull_outputsNull() {
        val dateRangePickerState = DateRangePickerState(
            locale = Locale.ENGLISH,
            initialSelectedStartDate = null,
            initialSelectedEndDate = null
        )

        assertNull(StatsTimeUtils().dateRangeToTimestampInterval(dateRangePickerState))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun dateRangeToTimestampInterval_startDateIsNotNull_EndDateIsNull_outputsNull() {
        val dateRangePickerState = DateRangePickerState(
            locale = Locale.ENGLISH,
            initialSelectedStartDate = LocalDate.of(2020, Month.MARCH, 1),
            initialSelectedEndDate = null
        )

        assertNull(StatsTimeUtils().dateRangeToTimestampInterval(dateRangePickerState))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun dateRangeToTimestampInterval_startDateIsNull_EndDateIsNotNull_outputsNull() {
        val dateRangePickerState = DateRangePickerState(
            locale = Locale.ENGLISH,
            initialSelectedStartDate = null,
            initialSelectedEndDate = LocalDate.of(2020, Month.MARCH, 1)
        )

        assertNull(StatsTimeUtils().dateRangeToTimestampInterval(dateRangePickerState))
    }

    private data class DateRangeTimeIntervalPair(
        val startLocalDate: LocalDate,  // inclusive
        val endLocalDate: LocalDate,    //inclusive

        val startTimestamp: Long,
        val endTimestamp: Long,
    )
    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun dateRangeToTimestampInterval_bothDatesAreNotNull_outputsNull() {
        val pairsByZoneId = mapOf(
            zoneIdOfAmericaNewYork!! to listOf(
                DateRangeTimeIntervalPair(LocalDate.of(2024, 2, 29), LocalDate.of(2024, 2, 29), 1709182800L, 1709269200L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 10), 1710046800L, 1710129600L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 11, 3), LocalDate.of(2024, 11, 3), 1730606400L, 1730696400L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 12, 31), LocalDate.of(2025, 1, 1), 1735621200L, 1735794000L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 2, 28), LocalDate.of(2024, 3, 1), 1709096400L, 1709355600L),
            ),
            zoneIdOfAsiaKolkata!! to listOf(
                DateRangeTimeIntervalPair(LocalDate.of(2024, 2, 29), LocalDate.of(2024, 2, 29), 1709145000L, 1709231400L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 10), 1710009000L, 1710095400L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 11, 3), LocalDate.of(2024, 11, 3), 1730572200L, 1730658600L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 12, 31), LocalDate.of(2025, 1, 1), 1735583400L, 1735756200L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 2, 28), LocalDate.of(2024, 3, 1), 1709058600L, 1709317800L),
            ),
            zoneIdOfAsiaTokyo!! to listOf(
                DateRangeTimeIntervalPair(LocalDate.of(2024, 2, 29), LocalDate.of(2024, 2, 29), 1709132400L, 1709218800L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 10), 1709996400L, 1710082800L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 11, 3), LocalDate.of(2024, 11, 3), 1730559600L, 1730646000L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 12, 31), LocalDate.of(2025, 1, 1), 1735570800L, 1735743600L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 2, 28), LocalDate.of(2024, 3, 1), 1709046000L, 1709305200L),
            ),
            zoneIdOfEuropeMoscow!! to listOf(
                DateRangeTimeIntervalPair(LocalDate.of(2024, 2, 29), LocalDate.of(2024, 2, 29), 1709154000L, 1709240400L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 10), 1710018000L, 1710104400L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 11, 3), LocalDate.of(2024, 11, 3), 1730581200L, 1730667600L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 12, 31), LocalDate.of(2025, 1, 1), 1735592400L, 1735765200L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 2, 28), LocalDate.of(2024, 3, 1), 1709067600L, 1709326800L),
            ),
            zoneIdOfUTC!! to listOf(
                DateRangeTimeIntervalPair(LocalDate.of(2024, 2, 29), LocalDate.of(2024, 2, 29), 1709164800L, 1709251200L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 10), 1710028800L, 1710115200L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 11, 3), LocalDate.of(2024, 11, 3), 1730592000L, 1730678400L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 12, 31), LocalDate.of(2025, 1, 1), 1735603200L, 1735776000L),
                DateRangeTimeIntervalPair(LocalDate.of(2024, 2, 28), LocalDate.of(2024, 3, 1), 1709078400L, 1709337600L),
            ),
        )


        pairsByZoneId.forEach { (zoneId, pairs) ->
            val utils = StatsTimeUtils(Clock.system(zoneId))

            pairs.forEach { pair ->
                val dateRangePickerState = DateRangePickerState(
                    locale = Locale.ENGLISH,
                    initialSelectedStartDate = pair.startLocalDate,
                    initialSelectedEndDate = pair.endLocalDate
                )
                assertEquals(
                    TimestampInterval(pair.startTimestamp, pair.endTimestamp),
                    utils.dateRangeToTimestampInterval(dateRangePickerState)
                )
            }
        }
    }


    @Test
    fun currentEpochSecond_correctOutput() {
        val listOfEpochSeconds: List<Long> = listOf(
            0,
            -3600,
            -86400,
            86400,
            268462926492634,
            kotlin.time.Instant.DISTANT_FUTURE.epochSeconds,
            kotlin.time.Instant.DISTANT_PAST.epochSeconds,
            Instant.MIN.epochSecond,
            Instant.MAX.epochSecond,
            Instant.EPOCH.epochSecond,
            1795206432,
        )

        val listOfZoneIds = listOf(
            zoneIdOfAmericaNewYork,
            zoneIdOfAsiaKolkata,
            zoneIdOfAsiaTokyo,
            zoneIdOfEuropeMoscow,
            zoneIdOfUTC
        )

        listOfEpochSeconds.forEach { epochSecond ->
            listOfZoneIds.forEach { zoneId ->
                val clock = Clock.fixed(Instant.ofEpochSecond(epochSecond), zoneId)
                assertEquals(epochSecond, StatsTimeUtils(clock).currentEpochSecond())
            }
        }
    }

    @Test
    fun currentTime_correctOutput() {
        val listOfLocalTimes = listOf(
            LocalTime.of(0, 0, 0),
            LocalTime.of(0, 0),
            LocalTime.of(0, 0, 0, 0),
            LocalTime.MAX,
            LocalTime.MIN,
            LocalTime.MIDNIGHT,
            LocalTime.NOON,
            LocalTime.of(1, 2, 3, 4),
            LocalTime.of(23, 59, 24, 60),
            LocalTime.of(23, 59, 59, 999_999_999)
        )

        val listOfZoneIds = listOf(
            zoneIdOfAmericaNewYork,
            zoneIdOfAsiaKolkata,
            zoneIdOfAsiaTokyo,
            zoneIdOfEuropeMoscow,
            zoneIdOfUTC
        )

        listOfZoneIds.forEach { zoneId ->
            listOfLocalTimes.forEach { localTime ->
                val clock = Clock.fixed(LocalDateTime.of(LocalDate.EPOCH, localTime).atZone(zoneId).toInstant(), zoneId)
                assertEquals(localTime, StatsTimeUtils(clock).currentTime())
            }
        }
    }

    @Test
    fun currentDate_checkIfMonthNumbersAreMappedCorrectly() {
        val datesToTest = listOf(
            LocalDate.of(2019, Month.JANUARY, 1),
            LocalDate.of(2019, Month.FEBRUARY, 1),
            LocalDate.of(2019, Month.MARCH, 1),
            LocalDate.of(2019, Month.APRIL, 1),
            LocalDate.of(2019, Month.MAY, 1),
            LocalDate.of(2019, Month.JUNE, 1),
            LocalDate.of(2019, Month.JULY, 1),
            LocalDate.of(2019, Month.SEPTEMBER, 1),
            LocalDate.of(2019, Month.OCTOBER, 1),
            LocalDate.of(2019, Month.NOVEMBER, 1),
            LocalDate.of(2019, Month.DECEMBER, 1),
        )

        val listOfZoneIds = listOf(
            zoneIdOfAmericaNewYork,
            zoneIdOfAsiaKolkata,
            zoneIdOfAsiaTokyo,
            zoneIdOfEuropeMoscow,
            zoneIdOfUTC
        )

        datesToTest.forEach { date ->
            listOfZoneIds.forEach { zoneId ->
                if(zoneId == null)
                    return@forEach

                val dateAsInstant = date.atTime(0,0).atZone(zoneId).toInstant()

                val clock = Clock.fixed(dateAsInstant, zoneId)
                assertEquals(date, StatsTimeUtils(clock).currentDate())
            }
        }
    }
}