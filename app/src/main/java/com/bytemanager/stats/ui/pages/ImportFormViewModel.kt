package com.bytemanager.stats.ui.pages

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytemanager.stats.data.repository.TimestampedBatteryStatesDbRepository
import com.bytemanager.stats.utils.ErrorHandler
import com.bytemanager.stats.utils.StatsCsvUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ImportFormViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val timestampedBatteryStatesDbRepository: TimestampedBatteryStatesDbRepository,
    private val errorHandler: ErrorHandler,
): ViewModel() {
    private val _importFormUiState: MutableStateFlow<ImportFormUiState> = MutableStateFlow(ImportFormUiState())
    val importFormUiState = _importFormUiState.asStateFlow()


    // ========== reset ImportForm Ui State ============

    fun resetUiState() {
        _importFormUiState.update {
            ImportFormUiState()
        }
    }



    // ======== Actions performing state change (F, F) -> (T, F) =========

    fun clickImportButton() {
        _importFormUiState.update { current ->
            current.copy(
                importButtonClickedIn = true,
                filePickerDialogEnabled = false
            )
        }
    }



    // ===== Actions performing state change (T, F) -> (F, F) ========

    fun processDropdownMenuDismissRequest() {
        _importFormUiState.update { current ->
            current.copy(
                importButtonClickedIn = false,
                filePickerDialogEnabled = false
            )
        }
    }



    // ======= Actions performing state change (T, F) -> (T, T) ========

    fun clickMergeOption() {
        _importFormUiState.update { current ->
            current.copy(
                importButtonClickedIn = true,
                filePickerDialogEnabled = true,
                importActionType = ImportActionType.MERGE,
            )
        }
    }

    fun clickReplaceOption() {
        _importFormUiState.update { current ->
            current.copy(
                importButtonClickedIn = true,
                filePickerDialogEnabled = true,
                importActionType = ImportActionType.REPLACE
            )
        }
    }



    // ======= Actions performing state change (T, T) -> (T, F) =======

    fun onInvalidUriReturn() {
        _importFormUiState.update { current ->
            current.copy(
                importButtonClickedIn = true,
                filePickerDialogEnabled = false,
                importActionType = null
            )
        }
    }








    // ======== Utility Functions ========

    fun processImportAction(uri: Uri) {
        val importActionType = importFormUiState.value.importActionType ?: return

        val action = if (importActionType == ImportActionType.MERGE) "Merge" else "Replace"
        var result = 0
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val inputStream = appContext.contentResolver.openInputStream(uri)

                    if (inputStream != null) {
                        val importingList = StatsCsvUtils.extractImportList(inputStream)

                        if (importActionType == ImportActionType.MERGE) {
                            result = errorHandler.catchAndReturnExceptionCodeSuspending {
                                timestampedBatteryStatesDbRepository.dbDao.insertListOfItemsWithAbort(
                                    importingList
                                )
                            }
                        } else if (importActionType == ImportActionType.REPLACE) {
                            result = errorHandler.catchAndReturnExceptionCodeSuspending {
                                timestampedBatteryStatesDbRepository.dbDao.replaceAllItems(
                                    importingList
                                )
                            }
                        }

                        inputStream.close()

                    } else {
                        result = 1
                    }
                }

                when (result) {
                    0 -> {
                        Toast.makeText(appContext, "$action successful", Toast.LENGTH_LONG).show()
                    }
                    1 -> {
                        Toast.makeText(
                            appContext,
                            "$action failed - empty import file",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    -1 -> {
                        Toast.makeText(
                            appContext,
                            "$action failed - timestamp already exists",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        Toast.makeText(
                            appContext,
                            "$action failed - error $result",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: CancellationException) {
                Toast.makeText(
                    appContext,
                    "$action failed - import task was cancelled unexpectedly",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("ImportFormViewModel", "$action failed - import task was cancelled unexpectedly: $e")
                throw e

            } catch (e: Exception) {
                Toast.makeText(
                    appContext,
                    "$action failed - Unexpected error occurred during import task",
                    Toast.LENGTH_LONG
                ).show()

                Log.e("ImportFormViewModel", "$action failed - Unexpected error occurred during import task: $e")

                val errorMessage = errorHandler.createErrorMessage(
                    function = "ImportFormViewModel::processImportAction",
                    error = e,
                    message = "Caught unexpected error. result = $result, importActionType = $importActionType"
                )

                errorHandler.logError(errorMessage)
            }
        }
    }
}

data class ImportFormUiState(
    val importButtonClickedIn: Boolean = false,
    val filePickerDialogEnabled: Boolean = false,
    val importActionType: ImportActionType? = null,
)

enum class ImportActionType {
    MERGE,
    REPLACE
}