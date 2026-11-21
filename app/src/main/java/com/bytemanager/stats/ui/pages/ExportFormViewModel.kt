package com.bytemanager.stats.ui.pages

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytemanager.stats.data.repository.TimestampedBatteryStatesDbRepository
import com.bytemanager.stats.data.types.TimestampInterval
import com.bytemanager.stats.utils.ErrorHandler
import com.bytemanager.stats.utils.StatsCsvUtils
import com.bytemanager.stats.utils.StatsCsvUtils.createUri
import com.bytemanager.stats.utils.StatsTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ExportFormViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val timestampedBatteryStatesDbRepository: TimestampedBatteryStatesDbRepository,
    private val errorHandler: ErrorHandler
): ViewModel() {
    private val _exportFormUiState: MutableStateFlow<ExportFormUiState> = MutableStateFlow(ExportFormUiState())
    val exportFormUiState = _exportFormUiState.asStateFlow()


    // ============= Reset Export Form Ui State ===========

    fun resetExportFormUiState() {
        _exportFormUiState.update {
            ExportFormUiState()
        }
    }



    // ===== Actions performing state change: (F, F, F) -> (T, T, F) =====

    fun clickExportButton() {
        _exportFormUiState.update { current ->
            current.copy(
                exportButtonClickedIn = true,
                dropdownMenuExpanded = true,
                datePickerDialogEnabled = false
            )
        }
    }




    // ===== Actions performing state change: (T, T, F) -> (F, F, F) =====

    fun processDropdownMenuDismissRequest() {
        _exportFormUiState.update { current ->
            current.copy(
                exportButtonClickedIn = false,
                dropdownMenuExpanded = false,
                datePickerDialogEnabled = false
            )
        }
    }

    fun clickExportAllOption() {
        viewModelScope.launch {
            val beforeDeleteAllCount = timestampedBatteryStatesDbRepository.dbDao.getCount().first()

            if(beforeDeleteAllCount == 0) {
                Toast.makeText(appContext, "Database is empty! No export file created.", Toast.LENGTH_LONG).show()
                Log.d("ExportFormViewModel", "Database is empty! No export file created.")

            } else {
                val resultOfExportAll = StatsCsvUtils.exportAll(
                    timestampedBatteryStatesDbRepository = timestampedBatteryStatesDbRepository,
                    errorHandler = errorHandler,
                    appContext = appContext
                )

                if(resultOfExportAll < 0) {
                    Toast.makeText(appContext, "Export failed!", Toast.LENGTH_LONG).show()
                    Log.e("ExportFormViewModel", "Export failed! resultOfExportAll = $resultOfExportAll")
                } else if(resultOfExportAll > 1) {
                    Toast.makeText(appContext, "Exported! (Warning: Could not export all the rows)", Toast.LENGTH_LONG).show()
                    Log.w("ExportFormViewModel", "Exported! (Warning: Could not export all the rows)")
                } else {
                    Toast.makeText(appContext, "Export successful. All rows exported.", Toast.LENGTH_LONG).show()
                    Log.d("ExportFormViewModel", "Export successful. All rows exported.")
                }
            }
        }

        resetExportFormUiState()
    }



    // ===== Actions performing state change: (T, T, F) -> (T, F, T) =====

    fun clickExportFromDateRangeOption() {
        _exportFormUiState.update { current ->
            current.copy(
                exportButtonClickedIn = true,
                dropdownMenuExpanded = false,
                datePickerDialogEnabled = true,
            )
        }
    }




    // ===== Actions performing state change: (T, F, T) -> (T, T, F) =====

    fun processDatePickerDialogDismissRequest() {
        _exportFormUiState.update { current ->
            current.copy(
                exportButtonClickedIn = true,
                dropdownMenuExpanded = true,
                datePickerDialogEnabled = false,
            )
        }
    }




    // ===== Actions performing state change: (T, F, T) -> (F, F, F) =====

    fun clickDatePickerDialogConfirmButton(
        dateRangePickerState: DateRangePickerState
    ) {
        viewModelScope.launch {
            val extractedTimestampInterval = StatsTimeUtils().dateRangeToTimestampInterval(dateRangePickerState)

            if(extractedTimestampInterval != null) {
                val statsTimeUtils = StatsTimeUtils()
                val startLocalDate = statsTimeUtils.epochMillisToLocalDate(extractedTimestampInterval.startTimestamp * 1000)
                val endLocalDate = statsTimeUtils.epochMillisToLocalDate(((extractedTimestampInterval.endTimestamp - 1) * 1000))

                val uri = createUri(appContext,"${appContext.packageName.toString().substring(startIndex = 4)}__${startLocalDate}__to__${endLocalDate}__at__${Instant.now().epochSecond}")
                if(uri == null) {
                    Log.d("ExportFromViewModel", "Error: uri is null")

                    Toast.makeText(appContext, "Export failed! Could not obtain export file URI", Toast.LENGTH_LONG).show()
                    Log.e("ExportFromViewModel", "Export failed! Could not obtain export file URI")

                } else {
                    var exportResult = 0
                    val result = errorHandler.catchAndReturnExceptionCodeSuspending {
                        val listOfItemsToExport = timestampedBatteryStatesDbRepository.dbDao
                            .getFromTimestampRange(
                                extractedTimestampInterval.startTimestamp,
                                extractedTimestampInterval.endTimestamp
                            )
                            .first()
                        if(listOfItemsToExport.isNotEmpty()) {
                            exportResult = StatsCsvUtils.exportBatteryStatesToCsv(
                                appContext,
                                uri,
                                listOfItemsToExport
                            )
                        }

                        if(listOfItemsToExport.isEmpty()) {
                            Toast.makeText(appContext, "No rows found in given date range! No export file created", Toast.LENGTH_LONG).show()
                            Log.d("ExportFormViewModel", "No rows found in given date range! No export file created")

                        } else {
                            if(exportResult == 0) {
                                Toast.makeText(appContext, "Error: Failed to export any rows!", Toast.LENGTH_LONG).show()
                                Log.e("ExportFormViewModel", "Error: Failed to export any rows!")

                            } else if(exportResult == listOfItemsToExport.size) {
                                Toast.makeText(appContext, "Export successful! All rows exported.", Toast.LENGTH_LONG).show()
                                Log.d("ExportFormViewModel", "Export successful! All $exportResult rows exported successfully")

                            } else if(exportResult < listOfItemsToExport.size) {
                                Toast.makeText(appContext, "Exported partially (Warning: exported rows count less than rows count in database)", Toast.LENGTH_LONG).show()
                                Log.w("ExportFormViewModel", "Exported partially (Warning: exported rows count less than rows count in database). exportResult = $exportResult, rowsCountInDatabase = ${listOfItemsToExport.size}")

                                val errorMessage = errorHandler.createErrorMessage(
                                    function = "ExportFormViewModel::clickDatePickerDialogConfirmButton",
                                    error = Exception("Warning: exported rows count less than rows count in database"),
                                    message = "exportResult = $exportResult, rowsCountInDatabase = ${listOfItemsToExport.size}"
                                )

                                errorHandler.catchAndReturnExceptionCodeSuspending {
                                    errorHandler.logError(errorMessage)
                                }
                            }
                        }
                    }

                    if(result != 0) {
                        Toast.makeText(appContext, "Export failed - error $result", Toast.LENGTH_LONG).show()
                        Log.d("ExportFormViewModel", "Export failed - error $result")
                    }
                }
            }
        }

        resetExportFormUiState()
    }



    // =================== DatePickerDialogConfirmButton ================

    fun enableDatePickerDialogConfirmButton() {
        _exportFormUiState.update { current ->
            current.copy(
                datePickerDialogConfirmButtonEnabled = true
            )
        }
    }

    fun disableDatePickerDialogConfirmButton() {
        _exportFormUiState.update { current ->
            current.copy(
                datePickerDialogConfirmButtonEnabled = false
            )
        }
    }





    // =============== Utility Functions ==================

    @OptIn(ExperimentalMaterial3Api::class)
    fun isValidDateRange(
        dateRangePickerState: DateRangePickerState
    ): Boolean {
        val startLocalDate = dateRangePickerState.getSelectedStartDate()
        val endLocalDate = dateRangePickerState.getSelectedEndDate()

        return (startLocalDate != null && endLocalDate != null)
    }
}

data class ExportFormUiState(
    val exportButtonClickedIn: Boolean = false,
    val dropdownMenuExpanded: Boolean = false,
    val datePickerDialogEnabled: Boolean = false,
    val datePickerDialogConfirmButtonEnabled: Boolean = false,
    val exportPendingAction: ExportPendingAction? = null,
)

data class ExportPendingAction(
    val timestampInterval: TimestampInterval? = null
)