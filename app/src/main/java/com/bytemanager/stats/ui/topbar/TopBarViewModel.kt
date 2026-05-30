package com.bytemanager.stats.ui.topbar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TopBarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TopBarUiState())
    val uiState = _uiState.asStateFlow()
    fun setIsOptionsMenuExpanded(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isOptionsMenuExpanded = value
            )
        }
    }
}

data class TopBarUiState (
    val isOptionsMenuExpanded: Boolean = false
)