package com.bytemanager.stats.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import com.bytemanager.stats.ui.theme.AppTypography
import com.bytemanager.stats.ui.theme.StatsTheme

@Composable
fun ResetForm(
    resetFormViewModel: ResetFormViewModel = hiltViewModel()
) {
    val uiState by resetFormViewModel.resetFormUiState.collectAsState()

    val isResetButtonClickedIn = uiState.isResetButtonClickedIn

    Column {
        Button(
            enabled = !isResetButtonClickedIn,
            onClick = {
                resetFormViewModel.clickResetButton()
            }
        ) {
            Text(
                text = stringResource(R.string.reset_button),
                style = AppTypography.labelLarge
            )
        }

        if(isResetButtonClickedIn) {
            Dialog(
                onDismissRequest = {
                    resetFormViewModel.onClickingDialogDismissButton()
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                )
            ) {
                StatsTheme {
                    Box(
                        modifier = Modifier
                            .width(350.dp)
                            .height(300.dp)
                            .background(
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                shape = RoundedCornerShape(15.dp)
                            )
                    ) {
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(10.dp)
                                .verticalScroll(
                                    state = rememberScrollState(),
                                )
                        ) {
                            Text(
                                text = stringResource(R.string.warning),
                                style = AppTypography.titleLarge
                            )

                            Spacer(Modifier.height(20.dp))

                            Text(
                                text = stringResource(R.string.warning_message),
                                style = AppTypography.bodyLarge
                            )

                            Spacer(Modifier.height(30.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Button(
                                    onClick = {
                                        resetFormViewModel.onClickingDialogDismissButton()
                                    }
                                ) {
                                    Text(
                                        text = stringResource(R.string.dismiss),
                                        style = AppTypography.labelLarge
                                    )
                                }

                                Button(
                                    onClick = {
                                        resetFormViewModel.onClickingDialogConfirmButton()
                                    }
                                ) {
                                    Text(
                                        text = stringResource(R.string.confirm),
                                        style = AppTypography.labelLarge
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