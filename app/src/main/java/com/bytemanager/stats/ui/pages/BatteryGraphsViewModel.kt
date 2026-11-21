package com.bytemanager.stats.ui.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytemanager.stats.data.repository.DataStoreRepository
import com.bytemanager.stats.data.repository.Settings
import com.bytemanager.stats.data.repository.TimestampedBatteryStatesDbRepository
import com.bytemanager.stats.data.types.GraphInterval
import com.bytemanager.stats.data.types.GraphType
import com.bytemanager.stats.data.types.TimestampedBatteryState
import com.bytemanager.stats.utils.BatteryStateRepositoryUtils
import com.bytemanager.stats.utils.StatsTimeUtils
import com.patrykandpatrick.vico.compose.cartesian.VicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.VicoZoomState
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.floor

@HiltViewModel
class BatteryGraphsViewModel  @Inject constructor(
    timestampedBatteryStatesDbRepository: TimestampedBatteryStatesDbRepository,
    dataStoreRepository: DataStoreRepository
): ViewModel() {
    private data class CombinedData(
        val listOfTimestampedBatteryState: List<TimestampedBatteryState> = emptyList(),

        val globalMinOfVoltage: Float? = null,
        val globalMaxOfVoltage: Float? = null,

        val globalMinOfTemperature: Float? = null,
        val globalMaxOfTemperature: Float? = null,

        val settings: Settings = Settings(),
    )

    private val _graphUiState = MutableStateFlow(GraphUiState())
    val graphUiState = _graphUiState.asStateFlow()

    private val flowOfCombinedData = combine(
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
        val timestampRange = StatsTimeUtils().localDateToTimestampInterval(date)
        val startTimestampOfTheDate = StatsTimeUtils().localDateToStartOfTheDayInEpochSeconds(date)



        CombinedData(
            listOfTimestampedBatteryState = timestampedBatteryStatesDbRepository.dbDao.getFromTimestampRange(
                timestampRange.startTimestamp,
                timestampRange.endTimestamp
            )
                .first()
                .map {
                    val timestampedBatteryState = BatteryStateRepositoryUtils.createTimestampedBatteryState(it, "-")
                    timestampedBatteryState.copy(
                        timestamp = it.timestamp - startTimestampOfTheDate
                    )
                },

            globalMinOfVoltage = globalMinOfVoltage,
            globalMaxOfVoltage = globalMaxOfVoltage,

            globalMinOfTemperature = globalMinOfTemperature,
            globalMaxOfTemperature = globalMaxOfTemperature,
        )
    }.combine(
        dataStoreRepository.flowOfSettings
    ) { combinedData, settings ->

        combinedData.copy(
            settings = settings
        )
    }

    init {
        viewModelScope.launch {
            flowOfCombinedData.collectLatest { combinedData ->
                _graphUiState.update { current ->
                    val graphsData = getGraphsData(combinedData)
                    current.copy(
                        graphsData = graphsData,
                    )
                }
            }
        }
    }

    private fun getGraphsData(combinedData: CombinedData): GraphsData {
        val listOfTimestamps: List<Float> = combinedData.listOfTimestampedBatteryState
            .mapNotNull { it.timestamp?.toFloat() }

        val listOfBatteryLevels: List<Float> = combinedData.listOfTimestampedBatteryState
            .mapNotNull { it.level?.toFloat() }
            .transformIf(combinedData.settings.isMovingAverageForVoltageGraphEnabled) {
                applyMovingAverageTransform(it)
            }

        val listOfBatteryVoltages: List<Float> = combinedData.listOfTimestampedBatteryState
            .mapNotNull { it.voltage }
            .transformIf(combinedData.settings.isMovingAverageForVoltageGraphEnabled) {
                applyMovingAverageTransform(it)
            }

        val listOfBatteryTemps: List<Float> = combinedData.listOfTimestampedBatteryState
            .mapNotNull { it.temperature }
            .transformIf(combinedData.settings.isMovingAverageForVoltageGraphEnabled) {
                applyMovingAverageTransform(it)
            }
            .transformIf(combinedData.settings.isFahrenheit) { list ->
                list.map {
                    BatteryStateRepositoryUtils.convertCelsiusToFahrenheits(it)
                }
            }


        val yStepOfVoltage = 0.1
        val yStepOfTemperature = if(combinedData.settings.isFahrenheit) 10.0 else 5.0

        val globalMinOfTemp = combinedData.globalMinOfTemperature?.transformIf(combinedData.settings.isFahrenheit) {
            BatteryStateRepositoryUtils.convertCelsiusToFahrenheits(it)
        }

        val globalMaxOfTemp = combinedData.globalMaxOfTemperature?.transformIf(combinedData.settings.isFahrenheit) {
            BatteryStateRepositoryUtils.convertCelsiusToFahrenheits(it)
        }

        val interpolator = if(combinedData.settings.isCutmullRomCurveEnabled) {
            LineCartesianLayer.Interpolator.catmullRom()
        } else {
            LineCartesianLayer.Interpolator.Sharp
        }

        val bottomAxisValueFormatter = { value: Double ->
            "${(value/3600).toInt()}:00"
        }

        return GraphsData(
            graphDataOfLevel = GraphData(
                graphType = GraphType.LEVEL,

                listOfTimestamps = listOfTimestamps,
                listOfValues = listOfBatteryLevels,

                yStep = 5.0,

                minY = 0.0,
                maxY = 100.0,

                startAxisValueFormatter = { value: Double ->
                    "${value.toInt()} %"
                },
                bottomAxisValueFormatter = bottomAxisValueFormatter,

                interpolator = interpolator
            ),


            graphDataOfVoltage = GraphData(
                graphType = GraphType.VOLTAGE,

                listOfTimestamps = listOfTimestamps,
                listOfValues = listOfBatteryVoltages,

                yStep = yStepOfVoltage,

                minY = calculateMinY(combinedData.globalMinOfVoltage, yStepOfVoltage),
                maxY = calculateMaxY(combinedData.globalMaxOfVoltage, yStepOfVoltage),

                startAxisValueFormatter = { value: Double ->
                    "${
                        String.format(
                            locale = Locale.getDefault(),
                            format = "%.1f",
                            value
                        )
                    } V"
                },
                bottomAxisValueFormatter = bottomAxisValueFormatter,

                interpolator = interpolator
            ),

            graphDataOfTemperature = GraphData(
                graphType = GraphType.TEMPERATURE,

                listOfTimestamps = listOfTimestamps,
                listOfValues = listOfBatteryTemps,

                yStep = yStepOfTemperature,

                minY = calculateMinY(globalMinOfTemp, yStepOfTemperature),
                maxY = calculateMaxY(globalMaxOfTemp, yStepOfTemperature),

                startAxisValueFormatter = { value: Double ->
                    "${value.toInt()} °${if(combinedData.settings.isFahrenheit)"F" else "C"}"
                },
                bottomAxisValueFormatter = bottomAxisValueFormatter,

                interpolator = interpolator
            ),
        )
    }





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
                dateSelected =  StatsTimeUtils().epochMillisToLocalDate(millis) ?: StatsTimeUtils().currentDate()
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

    fun calculateMinY(minValue: Float?, yStep: Double): Double? {
        minValue ?: return null
        return floor(minValue / yStep) * yStep - if (floor(minValue / yStep) == minValue / yStep) yStep else 0.0
    }

    fun calculateMaxY(maxValue: Float?, yStep: Double): Double? {
        maxValue ?: return null
        return ceil(maxValue / yStep) * yStep + if (ceil(maxValue / yStep) == maxValue / yStep) yStep else 0.0
    }

    fun applyMovingAverageTransform(listOfValues: List<Float>): List<Float> {
        return listOfValues.mapIndexed { index, initialValue ->
            val transformedValue = when (index) {
                0, listOfValues.lastIndex -> {
                    initialValue
                }
                1, listOfValues.lastIndex - 1 -> {
                    (listOfValues[index - 1] + initialValue + listOfValues[index + 1]) / 3f
                }
                else -> {
                    (listOfValues[index - 2] + listOfValues[index - 1] + initialValue + listOfValues[index + 1] + listOfValues[index + 2]) / 5f
                }
            }

            transformedValue
        }
    }

    private inline fun <T> T.transformIf(predicate: Boolean, block: (T) -> T): T {
        return if(predicate) block(this) else this
    }
}

data class GraphUiState(
    val graphType: GraphType = GraphType.UNSPECIFIED,
    val graphsData: GraphsData = GraphsData(),

    val dateSelected: LocalDate = StatsTimeUtils().currentDate(),
    val graphIntervalSelected: GraphInterval = GraphInterval.DAY_GRAPH,
    val isSelectGraphTypeButtonClicked: Boolean = false,
    val isSelectDateButtonClicked: Boolean = false,

    val scrollState: VicoScrollState = VicoScrollState(),
    val zoomState: VicoZoomState = VicoZoomState(
        zoomEnabled = true,
        initialZoom = Zoom.Content,
        minZoom = Zoom.Content,
        maxZoom = Zoom.fixed(4f)
    )
)

data class GraphsData(
    val graphDataOfLevel: GraphData = GraphData(),
    val graphDataOfVoltage: GraphData = GraphData(),
    val graphDataOfTemperature: GraphData = GraphData(),
)

data class GraphData(
    val graphType: GraphType = GraphType.UNSPECIFIED,

    val listOfTimestamps: List<Float> = emptyList(),
    val listOfValues: List<Float> = emptyList(),

    val yStep: Double? = null,

    val minY: Double? = null,
    val maxY: Double? = null,

    val startAxisValueFormatter: (value: Double) -> String = {""},
    val bottomAxisValueFormatter: (value: Double) -> String = {""},

    val interpolator: LineCartesianLayer.Interpolator = LineCartesianLayer.Interpolator.Sharp
)