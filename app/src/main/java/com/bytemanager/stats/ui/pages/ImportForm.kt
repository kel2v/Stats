package com.bytemanager.stats.ui.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
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
fun ImportForm(
    importFormViewModel: ImportFormViewModel = hiltViewModel()
) {
    val importFormUiState by importFormViewModel.importFormUiState.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if(uri == null) {
            importFormViewModel.onInvalidUriReturn()
        } else {
            importFormViewModel.processImportAction(uri)
        }

        importFormViewModel.resetUiState()
    }

    Column(
        modifier = Modifier
    ) {
        Button(
            enabled = !importFormUiState.importButtonClickedIn,
            onClick = {
                importFormViewModel.clickImportButton()
            }
        ) {
            Text(
                text = stringResource(R.string.import_label),
                style = AppTypography.labelLarge
            )
        }

        DropdownMenu(
            expanded = importFormUiState.importButtonClickedIn,

            onDismissRequest = {
                importFormViewModel.processDropdownMenuDismissRequest()
            },

            scrollState = rememberScrollState()
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.merge_into_existing_database_label),
                        style = AppTypography.labelMedium
                    )
                },

                onClick = {
                    importFormViewModel.clickMergeOption()
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.replace_existing_database_label),
                        style = AppTypography.labelMedium
                    )
                },

                onClick = {
                    importFormViewModel.clickReplaceOption()
                }
            )
        }

        LaunchedEffect(importFormUiState.filePickerDialogEnabled) {
            if(importFormUiState.filePickerDialogEnabled) {
                launcher.launch("*/*")
            }
        }
    }
}