package com.example.stats.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

// 1. Define your own qualifier
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationCoroutineScope

// 2. Provide it
@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {

    @ApplicationCoroutineScope
    @Singleton
    @Provides
    fun provideApplicationCoroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
}