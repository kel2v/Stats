package com.bytemanager.stats.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bytemanager.stats.R
import com.bytemanager.stats.data.types.GraphType
import com.bytemanager.stats.ui.graphs.DayWiseLineGraph
import com.bytemanager.stats.utils.StatsTimeUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun BatteryGraphs(
    batteryGraphsViewModel: BatteryGraphsViewModel = hiltViewModel(),
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val statsTimeUtils = StatsTimeUtils()
                val selectedDate = statsTimeUtils.millisToLocalDate(utcTimeMillis) ?: return false
                val today = statsTimeUtils.today()

                return selectedDate.isBefore(today) || selectedDate == today
            }
        }
    )

    val graphUiState by batteryGraphsViewModel.graphUiState.collectAsState()

    val date =  graphUiState.dateSelected
    val isSelectDateButtonClicked = graphUiState.isSelectDateButtonClicked

    val graphData by batteryGraphsViewModel.stateFlowOfGraphData.collectAsState()
    val scrollState = graphUiState.scrollState
    val zoomState = graphUiState.zoomState

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
    ) {
        DateSelectorMenu(
            datePickerState = datePickerState,
            isSelectDateButtonClicked = isSelectDateButtonClicked,
            date = date,

            onSelectDateButtonClick =  {
                batteryGraphsViewModel.clickSelectDateButton()
            },
            onClickDismissSelectDateDialog = {
                batteryGraphsViewModel.dismissSelectDateDialog()
            },
            onClickSelectDateDialogDismissButton = {
                batteryGraphsViewModel.dismissSelectDateDialog()
            },
            onClickSelectDateDialogConfirmButton = {
                batteryGraphsViewModel.onDatePickerConfirmButtonClick(datePickerState.selectedDateMillis)
            },
            onPrevButtonClick = {
                batteryGraphsViewModel.selectPreviousGraphDataInterval()
            },
            onNextButtonClick = {
                batteryGraphsViewModel.selectNextGraphDataInterval()
            }
        )

        Spacer(Modifier.height(50.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DayWiseLineGraph(
                graphName = stringResource(R.string.battery_level_graph_label),
                graphData,
                GraphType.LEVEL,
                scrollState = scrollState,
                zoomState = zoomState
            )

            Spacer(Modifier.height(40.dp))

            DayWiseLineGraph(
                graphName = stringResource(R.string.battery_temperature_graph_label),
                graphData,
                GraphType.TEMPERATURE,
                scrollState = scrollState,
                zoomState = zoomState
            )

            Spacer(Modifier.height(40.dp))

            DayWiseLineGraph(
                graphName = stringResource(R.string.battery_voltage_graph_label),
                graphData,
                GraphType.VOLTAGE,
                scrollState = scrollState,
                zoomState = zoomState
            )
        }
    }
}

@Composable
private fun DateSelectorMenu(
    datePickerState: DatePickerState,
    isSelectDateButtonClicked: Boolean,
    date: LocalDate,
    onSelectDateButtonClick: () -> Unit,
    onClickDismissSelectDateDialog: () -> Unit,
    onClickSelectDateDialogDismissButton: () -> Unit,
    onClickSelectDateDialogConfirmButton: () -> Unit,
    onPrevButtonClick: () -> Unit,
    onNextButtonClick: () -> Unit
    ) {

    Column {
        SelectedDateLabel(
            date = date,
            onSelectDateButtonClick = onSelectDateButtonClick,
            onPrevButtonClick = onPrevButtonClick,
            onNextButtonClick = onNextButtonClick
        )

        if(isSelectDateButtonClicked) {
            DatePickerDialog(
                onDismissRequest = onClickDismissSelectDateDialog,
                confirmButton = {
                    Button(
                        onClick = onClickSelectDateDialogConfirmButton
                    ) {
                        Text(
                            text = stringResource(R.string.confirm),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = onClickSelectDateDialogDismissButton
                    ) {
                        Text(
                            text = stringResource(R.string.dismiss),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun SelectedDateLabel(
    date: LocalDate,
    onSelectDateButtonClick: () -> Unit,
    onPrevButtonClick: () -> Unit,
    onNextButtonClick: () -> Unit
) {
    val today by remember { mutableStateOf(StatsTimeUtils().today()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))}" + if(date == today) " (${stringResource(R.string.today)})" else "",
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(Modifier.height(2.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = onSelectDateButtonClick
            ) {
                Text(
                    text = stringResource(R.string.select_date),
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Button(
                    onClick = onPrevButtonClick,
                ) {
                    Text(
                        text = stringResource(R.string.prev),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                Button(
                    onClick = onNextButtonClick,
                    enabled = !date.plusDays(1).isAfter(today)
                ) {
                    Text(
                        text = stringResource(R.string.next),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}