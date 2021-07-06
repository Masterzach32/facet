package io.facet.discord.extensions

import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.*
import reactor.core.publisher.*

/**
 * Extension to convert a nullable type of [T] to a [Mono] that emits the supplied
 * element when present, or complete if null.
 */
fun <T : Any> T?.toMonoOrEmpty(): Mono<T> = Mono.justOrEmpty(this)

/**
 * Awaits for the single value from the given [Mono], suspending the current coroutine and resuming when the
 * mono emits the resulting value or throws the corresponding exception if this publisher had produced error.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this
 * function immediately resumes with [CancellationException].
 *
 * @throws NoSuchElementException if mono does not emit any value
 */
suspend fun <T : Any> Mono<T>.await(): T = awaitFirst()

/**
 * Awaits for the single value from the given [Mono] or `null` value if none is emitted, suspending the current
 * coroutine and resuming when the mono returns the resulting value or throws the corresponding exception if this
 * publisher had produced error.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this
 * function immediately resumes with [CancellationException].
 */
suspend fun <T : Any> Mono<T>.awaitNullable(): T? = awaitFirstOrNull()

/**
 * Awaits for the completion signal from the given [Mono], suspending the current coroutine and resuming when the
 * mono emits the resulting value or throws the corresponding exception if this publisher had produced error.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this
 * function immediately resumes with [CancellationException].
 */
suspend fun Mono<*>.awaitComplete(): Unit = awaitNullable().let {}

/**
 * Awaits for the completion signal from the given [Mono], suspending the current coroutine and resuming when the
 * mono emits the resulting value or throws the corresponding exception if this publisher had produced error.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this
 * function immediately resumes with [CancellationException].
 */
@JvmName("awaitVoid")
suspend fun Mono<Void>.await(): Unit = awaitComplete()

