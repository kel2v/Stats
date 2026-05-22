package com.example.stats.hilt

import com.example.stats.interfaces.BatteryTempHistoryRepositoryInterface
import com.example.stats.repository.BatteryTempHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BatteryTempHistoryRepositoryModule {
    @Binds
    abstract fun bindBatteryTempHistoryRepositoryInterface(impl: BatteryTempHistoryRepository): BatteryTempHistoryRepositoryInterface
}