package com.muneer.tracker.ui

import androidx.compose.foundation.lazy.LazyListScope as ComposeLazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Small compatibility helpers kept local to the v3 UI while the dashboard is
 * being split into dedicated screens.
 */
@Composable
fun <T> rememberSaveable(calculation: () -> T): T = remember(calculation)

typealias LazyListScope = ComposeLazyListScope
