package com.bytemanager.stats.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bytemanager.stats.R
import com.bytemanager.stats.ui.theme.StatsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetForm(
    resetFormViewModel: ResetFormViewModel = hiltViewModel()
) {
    val uiState by resetFormViewModel.resetFormUiState.collectAsState()

    val isResetButtonClickedIn = uiState.isResetButtonClickedIn

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            enabled = !isResetButtonClickedIn,
            onClick = {
                resetFormViewModel.clickResetButton()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.reset_button),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        if(isResetButtonClickedIn) {
            Dialog(
                onDismissRequest = {
                    resetFormViewModel.onClickingDialogDismissButton()
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                )
            ) {
                StatsTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f)
                            .background(
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                shape = RoundedCornerShape(15.dp)
                            )
                    ) {
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(20.dp)
                                .verticalScroll(
                                    state = rememberScrollState(),
                                ).fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.warning),
                                style = MaterialTheme.typography.titleLarge,
                            )

                            Spacer(Modifier.height(20.dp))

                            Text(
                                text = stringResource(R.string.warning_message),
                                style = MaterialTheme.typography.bodyLarge,
                            )

                            Spacer(Modifier.height(30.dp))

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = {
                                        resetFormViewModel.onClickingDialogDismissButton()
                                    },
                                    modifier = Modifier.fillMaxWidth(0.7f)
                                ) {
                                    Text(
                                        text = stringResource(R.string.dismiss),
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                }

                                Spacer(Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        resetFormViewModel.onClickingDialogConfirmButton()
                                    },
                                    modifier = Modifier.fillMaxWidth(0.7f)
                                ) {
                                    Text(
                                        text = stringResource(R.string.confirm),
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}