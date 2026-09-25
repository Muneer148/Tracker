package com.muneer.tracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.LazyListScope as ComposeLazyListScope
import kotlinx.coroutines.runBlocking

/**
 * V3 UI compatibility helpers.
 *
 * The dashboard uses saveable-style state for transient screen controls. Until
 * the UI is split into dedicated screens, remember is sufficient because the
 * parent TrackerApp owns the navigation state and these fields are intentionally
 * ephemeral.
 */
@Composable
fun <T> rememberSaveable(calculation: () -> T): T = remember(calculation)

typealias LazyListScope = ComposeLazyListScope

/**
 * Allows the existing synchronous restore confirmation callback to invoke the
 * ViewModel's suspend restore operation. The restore itself remains transactional
 * inside the ViewModel/repository layer.
 */
inline fun <R> runCatching(block: suspend () -> R): Result<R> =
    runBlocking { kotlin.runCatching { block() } }
