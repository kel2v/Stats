package com.bytemanager.stats.ui.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            enabled = !importFormUiState.importButtonClickedIn,
            onClick = {
                importFormViewModel.clickImportButton()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.import_label),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        DropdownMenu(
            expanded = importFormUiState.importButtonClickedIn,

            onDismissRequest = {
                importFormViewModel.processDropdownMenuDismissRequest()
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
                            text = stringResource(R.string.merge_into_existing_database_label),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },

                    onClick = {
                        importFormViewModel.clickMergeOption()
                    }
                )

                Spacer(Modifier.height(20.dp))

                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.replace_existing_database_label),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },

                    onClick = {
                        importFormViewModel.clickReplaceOption()
                    }
                )
            }
        }

        LaunchedEffect(importFormUiState.filePickerDialogEnabled) {
            if(importFormUiState.filePickerDialogEnabled) {
                launcher.launch("*/*")
            }
        }
    }
}