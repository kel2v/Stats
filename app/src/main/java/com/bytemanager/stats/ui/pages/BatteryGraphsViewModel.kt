package com.bytemanager.stats.ui.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytemanager.stats.data.repository.DataStoreRepository
import com.bytemanager.stats.data.repository.TimestampedBatteryStatesDbRepository
import com.bytemanager.stats.data.types.GraphInterval
import com.bytemanager.stats.data.types.GraphType
import com.bytemanager.stats.data.types.TimestampedBatteryLevel
import com.bytemanager.stats.data.types.TimestampedBatteryTemp
import com.bytemanager.stats.data.types.TimestampedBatteryVoltage
import com.bytemanager.stats.data.types.convertToTimestampedBatteryLevel
import com.bytemanager.stats.data.types.convertToTimestampedBatteryTemp
import com.bytemanager.stats.data.types.convertToTimestampedBatteryVoltage
import com.bytemanager.stats.utils.BatteryStateRepositoryUtils
import com.bytemanager.stats.utils.StatsTimeUtils
import com.patrykandpatrick.vico.compose.cartesian.VicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.VicoZoomState
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BatteryGraphsViewModel  @Inject constructor(
    timestampedBatteryStatesDbRepository: TimestampedBatteryStatesDbRepository,
    dataStoreRepository: DataStoreRepository
): ViewModel() {
    private val dbDao = timestampedBatteryStatesDbRepository.dbDao

    private val _graphUiState = MutableStateFlow(GraphUiState())
    val graphUiState = _graphUiState.asStateFlow()

    val stateFlowOfSettings = dataStoreRepository.getStateFlowOfSettings(viewModelScope)

    val stateFlowOfGraphData = combine(
        graphUiState,
        timestampedBatteryStatesDbRepository.dbDao.globalMinOfTemperature().map { BatteryStateRepositoryUtils.getTemperatureInCelsius(it) },
        timestampedBatteryStatesDbRepository.dbDao.globalMaxOfTemperature().map { BatteryStateRepositoryUtils.getTemperatureInCelsius(it) },
        timestampedBatteryStatesDbRepository.dbDao.globalMinOfVoltage().map { BatteryStateRepositoryUtils.getVoltage(it) },
        timestampedBatteryStatesDbRepository.dbDao.globalMaxOfVoltage().map { BatteryStateRepositoryUtils.getVoltage(it) },
    ) {
        uiState,
        globalMinOfTemperature,
        globalMaxOfTemperature,
        globalMinOfVoltage,
        globalMaxOfVoltage ->

        val date = uiState.dateSelected
        val timestampRange = StatsTimeUtils().localDayToTimestampInterval(date)
        val startTimestampOfTheDate = StatsTimeUtils().localDateToStartOfTheDayInEpochSeconds(date)

        val listOfTimestampedBatteryTemp =
            dbDao.getListOfTimestampedRawBatteryTempFromTimestampRange(
                timestampRange.startTimestamp,
                timestampRange.endTimestamp
            )
                .first()
                .mapNotNull { item ->
                    item.convertToTimestampedBatteryTemp()
                }.map { item ->
                    item.copy(
                        timestamp = item.timestamp - startTimestampOfTheDate
                    )
                }

        val listOfTimestampedBatteryVoltage =
            dbDao.getListOfTimestampedRawBatteryVoltageFromTimestampRange(
                timestampRange.startTimestamp,
                timestampRange.endTimestamp
            )
                .first()
                .mapNotNull { item ->
                    item.convertToTimestampedBatteryVoltage()
                }.map { item ->
                    item.copy(
                        timestamp = item.timestamp - startTimestampOfTheDate
                    )
                }

        val listOfTimestampedBatteryLevel =
            dbDao.getListOfTimestampedRawBatteryLevelFromTimestampRange(
                timestampRange.startTimestamp,
                timestampRange.endTimestamp
            )
                .first()
                .mapNotNull { item ->
                    item.convertToTimestampedBatteryLevel()
                }.map { item ->
                    item.copy(
                        timestamp = item.timestamp - startTimestampOfTheDate
                    )
                }

        val isFahrenheit = stateFlowOfSettings.value.isFahrenheit

        GraphData(
            graphDataOfLevel = GraphDataOfLevel(
                listOfTimestampedBatteryLevel = listOfTimestampedBatteryLevel,
            ),

            graphDataOfVoltage = GraphDataOfVoltage(
                listOfTimestampedBatteryVoltage = listOfTimestampedBatteryVoltage,
                globalMinOfVoltage = globalMinOfVoltage,
                globalMaxOfVoltage = globalMaxOfVoltage
            ),

            graphDataOfTemperature = GraphDataOfTemperature(
                listOfTimestampedBatteryTemp = listOfTimestampedBatteryTemp,
                globalMinOfTemperature = globalMinOfTemperature,
                globalMaxOfTemperature = globalMaxOfTemperature
            ),

            isFahrenheit = isFahrenheit
        )
    }
        .combine(stateFlowOfSettings) { graphData, settings ->
            graphData.copy(
                isFahrenheit = settings.isFahrenheit,
                isMovingAverageForVoltageGraphEnabled = settings.isMovingAverageForVoltageGraphEnabled,
                isCutmullRomCurveEnabled = settings.isCutmullRomCurveEnabled
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GraphData()
        )








    // state change (SGTBC = F, SDBC = T) -> (SGTBC = F, SDBC = F)

    fun dismissSelectDateDialog() {
        _graphUiState.update { current ->
            current.copy(
                isSelectDateButtonClicked = false
            )
        }
    }

    fun onDatePickerConfirmButtonClick(millis: Long?) {
        _graphUiState.update { current ->
            current.copy(
                isSelectDateButtonClicked = false
            )
        }

        extractSelectedDate(millis)
    }





    // state change (SGTBC = F, SDBC = F) -> (SGTBC = F, SDBC = T)

    fun clickSelectDateButton() {
        _graphUiState.update { current ->
            current.copy(
                isSelectDateButtonClicked = true
            )
        }
    }













    private fun extractSelectedDate(millis: Long?) {
        _graphUiState.update { current ->
            current.copy(
                dateSelected =  StatsTimeUtils().millisToLocalDate(millis) ?: StatsTimeUtils().today()
            )
        }
    }

    fun selectPreviousGraphDataInterval() {
        if(graphUiState.value.graphIntervalSelected == GraphInterval.DAY_GRAPH) {
            _graphUiState.update { current ->
                current.copy(
                    dateSelected =  current.dateSelected.minusDays(1)
                )
            }
        }
    }

    fun selectNextGraphDataInterval() {
        if(graphUiState.value.graphIntervalSelected == GraphInterval.DAY_GRAPH) {
            _graphUiState.update { current ->
                current.copy(
                    dateSelected =  current.dateSelected.plusDays(1)
                )
            }
        }
    }
}

data class GraphUiState(
    val graphType: GraphType = GraphType.UNSPECIFIED,

    val dateSelected: LocalDate = StatsTimeUtils().today(),
    val graphIntervalSelected: GraphInterval = GraphInterval.DAY_GRAPH,
    val isSelectGraphTypeButtonClicked: Boolean = false,
    val isSelectDateButtonClicked: Boolean = false,

    val isFahrenheit: Boolean? = null,

    val scrollState: VicoScrollState = VicoScrollState(),
    val zoomState: VicoZoomState = VicoZoomState(
        zoomEnabled = true,
        initialZoom = Zoom.Content,
        minZoom = Zoom.Content,
        maxZoom = Zoom.fixed(4f)
    )
)

data class GraphData(
    val graphDataOfLevel: GraphDataOfLevel = GraphDataOfLevel(),
    val graphDataOfVoltage: GraphDataOfVoltage = GraphDataOfVoltage(),
    val graphDataOfTemperature: GraphDataOfTemperature = GraphDataOfTemperature(),

    val isFahrenheit: Boolean = false,
    val isMovingAverageForVoltageGraphEnabled: Boolean = true,
    val isCutmullRomCurveEnabled: Boolean = true,
)

data class GraphDataOfTemperature(
    val listOfTimestampedBatteryTemp: List<TimestampedBatteryTemp> = emptyList(),
    val globalMinOfTemperature: Float? = null,
    val globalMaxOfTemperature: Float? = null,
)

data class GraphDataOfVoltage(
    val listOfTimestampedBatteryVoltage: List<TimestampedBatteryVoltage> = emptyList(),
    val globalMinOfVoltage: Float? = null,
    val globalMaxOfVoltage: Float? = null,
)

data class GraphDataOfLevel(
    val listOfTimestampedBatteryLevel: List<TimestampedBatteryLevel> = emptyList(),
    val globalMinOfLevel: Int = 0,
    val globalMaxOfLevel: Int = 100
)