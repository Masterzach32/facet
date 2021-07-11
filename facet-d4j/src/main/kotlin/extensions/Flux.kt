package io.facet.discord.extensions

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*
import reactor.core.publisher.*

/**
 * Suspends the current coroutine, and returns all elements emitted by the [Flux] in a [List].
 */
suspend fun <T : Any> Flux<T>.await() = asFlow().toList()

/**
 * Suspends the current coroutine, and returns when the [Flux] completes.
 */
@JvmName("awaitVoid")
suspend fun Flux<Void>.await() = asFlow().collect()
