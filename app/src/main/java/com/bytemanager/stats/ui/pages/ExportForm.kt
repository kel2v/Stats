package com.bytemanager.stats.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bytemanager.stats.R
import com.bytemanager.stats.ui.theme.AppTypography

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
        modifier = Modifier
    ) {
        Button(
            enabled = !exportFormUiState.exportButtonClickedIn,
            onClick = {
                exportFormViewModel.clickExportButton()
            },
        ) {
            Text(
                text = stringResource(R.string.export),
                style = AppTypography.labelLarge
            )
        }


        DropdownMenu(
            expanded = exportFormUiState.dropdownMenuExpanded,
            onDismissRequest = {
                exportFormViewModel.processDropdownMenuDismissRequest()
            },
            scrollState = rememberScrollState()
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.export_all_data_label),
                        style = AppTypography.labelMedium
                    )
                },
                onClick = {
                    exportFormViewModel.clickExportAllOption()
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.export_from_date_range_label),
                        style = AppTypography.labelMedium
                    )
                },
                onClick = {
                    exportFormViewModel.clickExportFromDateRangeOption()
                },
            )
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
                        style = AppTypography.labelLarge
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
                        style = AppTypography.labelLarge
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