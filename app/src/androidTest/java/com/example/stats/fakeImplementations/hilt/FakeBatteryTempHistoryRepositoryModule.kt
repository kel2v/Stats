package com.example.stats.fakeImplementations.hilt

import com.example.stats.fakeImplementations.repository.FakeBatteryTempHistoryRepository
import com.example.stats.hilt.BatteryTempHistoryRepositoryModule
import com.example.stats.interfaces.BatteryTempHistoryRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [BatteryTempHistoryRepositoryModule::class]
)
abstract class FakeBatteryTempHistoryRepositoryModule {
    @Binds
    abstract fun bindBatteryTempHistoryRepositoryInterface(impl: FakeBatteryTempHistoryRepository): BatteryTempHistoryRepositoryInterface
}