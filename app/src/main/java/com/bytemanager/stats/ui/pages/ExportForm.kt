package com.bytemanager.stats.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bytemanager.stats.R

@Composable
fun ExportForm(
    exportFormViewModel: ExportFormViewModel = hiltViewModel()
) {
    val exportFormUiState by exportFormViewModel.exportFormUiState.collectAsState()
    val dateRangePickerState = rememberDateRangePickerState()

    LaunchedEffect(dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis) {
        if(exportFormViewModel.isValidDateRange(dateRangePickerState)) {
            exportFormViewModel.enableDatePickerDialogConfirmButton()
        } else {
            exportFormViewModel.disableDatePickerDialogConfirmButton()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            enabled = !exportFormUiState.exportButtonClickedIn,
            onClick = {
                exportFormViewModel.clickExportButton()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.export),
                style = MaterialTheme.typography.labelLarge,
            )
        }


        DropdownMenu(
            expanded = exportFormUiState.dropdownMenuExpanded,
            onDismissRequest = {
                exportFormViewModel.processDropdownMenuDismissRequest()
            },
            scrollState = rememberScrollState(),
            properties = PopupProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth(0.6f),
        ) {
            Column(
                Modifier.padding(10.dp)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.export_all_data_label),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    onClick = {
                        exportFormViewModel.clickExportAllOption()
                    }
                )

                Spacer(Modifier.height(20.dp))

                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.export_from_date_range_label),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    onClick = {
                        exportFormViewModel.clickExportFromDateRangeOption()
                    },
                )
            }
        }
    }

    if(exportFormUiState.datePickerDialogEnabled) {
        DatePickerDialog(
            onDismissRequest = {
                exportFormViewModel.processDatePickerDialogDismissRequest()
            },

            dismissButton = {
                Button(
                    enabled = true,
                    onClick = {
                        exportFormViewModel.processDatePickerDialogDismissRequest()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.dismiss),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            },

            confirmButton = {
                Button(
                    enabled = exportFormUiState.datePickerDialogConfirmButtonEnabled,
                    onClick = {
                        exportFormViewModel.clickDatePickerDialogConfirmButton(dateRangePickerState)
                    },
                ) {
                    Text(
                        text = stringResource(R.string.confirm),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            },
        ) {
            DateRangePicker(
                state = dateRangePickerState
            )
        }
    }
}