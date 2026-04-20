package com.example.stats.fakeImplementations.models

import com.example.stats.data_structure.BatteryState
import com.example.stats.interfaces.BatteryViewModelInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class FakeBatteryViewModel: BatteryViewModelInterface {
    override val batteryStateStateFlow: StateFlow<BatteryState> = flow {
        emit(BatteryState(23, 4.7f, "Charging", 34.3f, "LiPo", "Good"))
    }.stateIn(
        CoroutineScope(SupervisorJob() + Dispatchers.Default),
            SharingStarted.Eagerly,
            BatteryState(-1, Float.MAX_VALUE * -1, "Not available", Float.MAX_VALUE * -1, "Not available", "Not available")
    )
}