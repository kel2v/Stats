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
import com.bytemanager.stats.ui.pages.GraphsData
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

@Composable
fun DayWiseLineGraph(
    graphName: String,
    graphsData: GraphsData,
    graphType: GraphType,
    scrollState: VicoScrollState,
    zoomState: VicoZoomState
) {
    val graphData = when(graphType) {
        GraphType.LEVEL -> graphsData.graphDataOfLevel
        GraphType.TEMPERATURE -> graphsData.graphDataOfTemperature
        GraphType.VOLTAGE -> graphsData.graphDataOfVoltage
        GraphType.UNSPECIFIED -> return
    }

    val xList = graphData.listOfTimestamps
    val yList = graphData.listOfValues

    val minY = graphData.minY
    val maxY = graphData.maxY

    val yStep = graphData.yStep

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
            minY == null ||
            maxY == null
        ) {
            Text(
                text = stringResource(R.string.no_data_found),
                style = MaterialTheme.typography.bodySmall
            )

            return
        }

        val modelProducer = remember { CartesianChartModelProducer() }

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
                                interpolator = graphData.interpolator,
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
                        graphData.startAxisValueFormatter(value)
                    }
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = {_, value, _ ->
                        graphData.bottomAxisValueFormatter(value)
                    },
                ),
                getXStep = { _, _, _ ->
                    3600.0
                },
            ),
            modelProducer = modelProducer,
            scrollState = scrollState,
            zoomState = zoomState,
            modifier = Modifier.padding(horizontal = 2.dp),
            chartAreaHeight = 400.dp
        )
    }
}