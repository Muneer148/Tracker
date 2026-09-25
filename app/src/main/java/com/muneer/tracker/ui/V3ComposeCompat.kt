package com.muneer.tracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable as composeRememberSaveable
import androidx.compose.foundation.lazy.LazyListScope as ComposeLazyListScope

/**
 * Compatibility aliases used by the v3 UI while keeping TrackerApp.kt focused on UI composition.
 */
typealias LazyListScope = ComposeLazyListScope

@Composable
fun <T : Any> rememberSaveable(init: () -> T): T = composeRememberSaveable(init = init)
