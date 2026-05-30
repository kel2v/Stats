package com.example.stats

import com.example.stats.hilt.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class ApplicationCoroutineScopeProvider @Inject constructor(
    @ApplicationCoroutineScope val scope: CoroutineScope
)