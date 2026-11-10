package com.bytemanager.stats.ui.graphs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bytemanager.stats.R
import com.bytemanager.stats.data.types.GraphType
import com.bytemanager.stats.ui.pages.GraphData
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.VicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.VicoZoomState
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun DayWiseLineGraph(
    graphName: String,
    graphData: GraphData,
    graphType: GraphType,
    scrollState: VicoScrollState,
    zoomState: VicoZoomState
) {
    var xList: List<Float>
    var yList: List<Float>
    var minValue: Float?
    var maxValue: Float?

    var minY: Double
    var maxY: Double
    var yStep: Double

    when (graphType) {
        GraphType.LEVEL -> {
            xList = graphData.graphDataOfLevel.listOfTimestampedBatteryLevel.map { it.timestamp.toFloat() }
            yList = graphData.graphDataOfLevel.listOfTimestampedBatteryLevel.map { it.level.toFloat() }

            minValue = graphData.graphDataOfLevel.globalMinOfLevel.toFloat()
            maxValue = graphData.graphDataOfLevel.globalMaxOfLevel.toFloat()

        }
        GraphType.VOLTAGE -> {
            xList = graphData.graphDataOfVoltage.listOfTimestampedBatteryVoltage.map { it.timestamp.toFloat() }
            yList = graphData.graphDataOfVoltage.listOfTimestampedBatteryVoltage.map { it.voltage }

            minValue = graphData.graphDataOfVoltage.globalMinOfVoltage
            maxValue = graphData.graphDataOfVoltage.globalMaxOfVoltage

        }
        GraphType.TEMPERATURE -> {
            xList = graphData.graphDataOfTemperature.listOfTimestampedBatteryTemp.map { it.timestamp.toFloat() }
            yList = graphData.graphDataOfTemperature.listOfTimestampedBatteryTemp.map { it.temperature }

            minValue = graphData.graphDataOfTemperature.globalMinOfTemperature
            maxValue = graphData.graphDataOfTemperature.globalMaxOfTemperature
        }
        else -> {
            return
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(5.dp),
                color = Color.LightGray
            )
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(
            text = graphName,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(2.dp))

        if(
            xList.isEmpty() ||
            yList.isEmpty() ||
            minValue == null ||
            maxValue == null
        ) {
            Text(
                text = stringResource(R.string.no_data_found),
                style = MaterialTheme.typography.bodySmall
            )

            return
        }

        when (graphType) {
            GraphType.LEVEL -> {
                minY = minValue.toDouble()
                maxY = maxValue.toDouble()

                yStep = 5.0
            }
            GraphType.VOLTAGE -> {
                minY = floor(minValue / 0.1) * 0.1 - 0.1
                maxY = ceil(maxValue / 0.1) * 0.1 + 0.1

                yStep = 0.1
            }
            else -> {
                minY =
                    floor(minValue / 5.0) * 5.0 - if (floor(minValue / 5.0) == minValue / 5.0) 5.0 else 0.0
                maxY =
                    ceil(maxValue / 5.0) * 5.0 + if (ceil(maxValue / 5.0) == maxValue / 5.0) 5.0 else 0.0

                yStep = 5.0
            }
        }

        if(graphType == GraphType.TEMPERATURE && graphData.isFahrenheit) {
            yList = yList.map { (it * 1.8f) + 32f }

            minY = (minY * 1.8f) + 32
            maxY = (maxY * 1.8f) + 32

            minY = floor(minY / 10) * 10
            maxY = ceil(maxY / 10) * 10

            yStep = 10.0
        }

        val modelProducer = remember { CartesianChartModelProducer() }

        if(graphData.isMovingAverageForVoltageGraphEnabled) {
            val smoothenedYList: List<Float> = yList.mapIndexed { index, f ->
                val value = if(index == 0 || index == yList.lastIndex) {
                    f
                } else if(index == 1 || index == yList.lastIndex-1) {
                    (yList[index-1] + f + yList[index+1])/3f
                }
                else {
                    (yList[index-2] + yList[index-1] + f + yList[index+1] + yList[index+2])/5f
                }

                value
            }

            yList = smoothenedYList
        }

        LaunchedEffect(graphData) {
            modelProducer.runTransaction {
                lineModel {
                    series(
                        x = xList,
                        y = yList
                    )
                }
            }
        }


        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider =
                        LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(
                                fill = LineCartesianLayer.LineFill.single(Fill(MaterialTheme.colorScheme.primary)),
                                areaFill =
                                    LineCartesianLayer.AreaFill.single(
                                        Fill(
                                            Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), Color.Transparent))
                                        )
                                    ),
                                interpolator = if(graphData.isCutmullRomCurveEnabled) LineCartesianLayer.Interpolator.catmullRom() else LineCartesianLayer.Interpolator.Sharp,
                            )
                        ),
                    rangeProvider = CartesianLayerRangeProvider.fixed(
                        minX = 0.0,
                        maxX = 86400.0,
                        minY = minY,
                        maxY = maxY
                    ),
                ),
                startAxis = VerticalAxis.rememberStart(
                    itemPlacer = VerticalAxis.ItemPlacer.step({yStep}),
                    valueFormatter = {_, value, _ ->
                        when (graphType) {
                            GraphType.TEMPERATURE -> {
                                "${value.toInt()} °${if(graphData.isFahrenheit)"F" else "C"}"
                            }
                            GraphType.LEVEL -> {
                                "${value.toInt()} %"
                            }
                            GraphType.VOLTAGE -> {
                                "${
                                    String.format(
                                        locale = Locale.getDefault(), 
                                        format = "%.1f", 
                                        value
                                    )
                                } V"
                            }
                        }
                    }
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = {_, value, _ ->
                        "${(value/3600).toInt()}:00"
                    },
                ),
                getXStep = { _, _, _ ->
                    3600.0
                },
            ),
            modelProducer = modelProducer,
            scrollState = scrollState,
            zoomState = zoomState,
            modifier = Modifier
                .padding(horizontal = 2.dp),
            chartAreaHeight = 400.dp
        )
    }
}