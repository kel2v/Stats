package com.bytemanager.stats.ui.pages

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytemanager.stats.data.repository.TimestampedBatteryStatesDbRepository
import com.bytemanager.stats.utils.ErrorHandler
import com.bytemanager.stats.utils.StatsCsvUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetFormViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val timestampedBatteryStatesDbRepository: TimestampedBatteryStatesDbRepository,
    private val errorHandler: ErrorHandler
): ViewModel() {
    private val _resetFormUiState: MutableStateFlow<ResetFormUiState> = MutableStateFlow(ResetFormUiState())
    val resetFormUiState = _resetFormUiState.asStateFlow()

    fun clickResetButton() {
        _resetFormUiState.update { current ->
            current.copy(
                isResetButtonClickedIn = true
            )
        }
    }

    fun onClickingDialogDismissButton() {
        _resetFormUiState.update { current ->
            current.copy(
                isResetButtonClickedIn = false
            )
        }
    }

    fun onClickingDialogConfirmButton() {
        viewModelScope.launch {
            val resultOfExportAll = StatsCsvUtils.exportAll(
                timestampedBatteryStatesDbRepository = timestampedBatteryStatesDbRepository,
                errorHandler = errorHandler,
                appContext = appContext
            )

            if(resultOfExportAll < 0) {
                Toast.makeText(appContext, "Resetting ABORTED: Export failed!", Toast.LENGTH_LONG).show()
                Log.e("ResetFormViewModel", "Resetting ABORTED: Export failed! resultOfExportAll = $resultOfExportAll")

            } else {
                if(resultOfExportAll > 1) {
                    Log.w("ResetFormViewModel", "Warning! Could not export all the rows for backup. Continuing to reset database ...")
                }

                var beforeDeleteAllCount = 0
                var deleteAllResult = 0

                val resultOfCatchAndReturnExceptionCodeSuspending = errorHandler.catchAndReturnExceptionCodeSuspending {
                    beforeDeleteAllCount = timestampedBatteryStatesDbRepository.dbDao.getCount().first()

                    if(beforeDeleteAllCount != 0) {
                        deleteAllResult = timestampedBatteryStatesDbRepository.dbDao.deleteAll()
                    }
                }

                if(resultOfCatchAndReturnExceptionCodeSuspending == 0) {
                    if(beforeDeleteAllCount == 0) {
                        Toast.makeText(appContext, "Database is already empty!", Toast.LENGTH_LONG).show()
                        Log.d("ResetFormViewModel", "Database is already empty!")

                    } else if(deleteAllResult == beforeDeleteAllCount) {
                        Toast.makeText(appContext, "Reset successful. All rows exported for backup", Toast.LENGTH_LONG).show()
                        Log.d("ResetFormViewModel", "Reset successful. All rows exported for backup")

                    } else {
                        val afterDeleteDbCount = timestampedBatteryStatesDbRepository.dbDao.getCount().first()

                        errorHandler.logError(
                            errorHandler.createErrorMessage(
                                function = "ResetFormViewModel::onClickingDialogConfirmButton",
                                error = Exception("Warning: Unable to fully reset!"),
                                message = "beforeDeleteAllCount = $beforeDeleteAllCount, deleteAllResult = $deleteAllResult, afterDeleteDbCount = $afterDeleteDbCount"
                            )
                        )

                        Toast.makeText(appContext, "Warning: Unable to fully reset! Try 'clear all data' in app settings", Toast.LENGTH_LONG).show()
                        Log.w("ResetFormViewModel", "Warning: Unable to fully reset! Try 'clear all data' in app settings")
                    }
                }
            }
        }

        resetUiState()
    }

    fun resetUiState() {
        _resetFormUiState.update {
            ResetFormUiState()
        }
    }
}

data class ResetFormUiState(
    val isResetButtonClickedIn: Boolean = false
)