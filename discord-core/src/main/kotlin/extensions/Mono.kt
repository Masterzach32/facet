package io.facet.discord.extensions

import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.*
import reactor.core.publisher.*
import kotlin.coroutines.*

/**
 * Extension to convert a nullable type of [T] to a [Mono] that emits the supplied
 * element when present, or complete if null.
 */
fun <T : Any> T?.toMonoOrEmpty(): Mono<T> = Mono.justOrEmpty(this)

/**
 * Awaits for the single value from the given [Mono] without blocking a thread and returns the resulting
 * value or throws the corresponding exception if this publisher had produced error.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this
 * function immediately resumes with [CancellationException].
 *
 * @throws NoSuchElementException if mono does not emit any value
 */
suspend fun <T : Any> Mono<T>.await(): T = awaitFirst()

/**
 * Awaits for the single value from the given [Mono] or `null` value if none is emitted without blocking a thread
 * and returns the resulting value or throws the corresponding exception if this publisher had produced error.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this
 * function immediately resumes with [CancellationException].
 *
 * @throws NoSuchElementException if mono does not emit any value
 */
suspend fun <T : Any> Mono<T>.awaitNullable(): T? = awaitFirstOrNull()

/**
 * Awaits for the completion signal from the given [Mono] without blocking a thread and returns the resulting
 * value or throws the corresponding exception if this publisher had produced error.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this
 * function immediately resumes with [CancellationException].
 *
 * @throws NoSuchElementException if mono does not emit any value
 */
suspend fun Mono<*>.awaitComplete(): Unit = awaitNullable().let { Unit }

/**
 * Awaits for the completion signal from the given [Mono] without blocking a thread and returns the resulting
 * value or throws the corresponding exception if this publisher had produced error.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this
 * function immediately resumes with [CancellationException].
 *
 * @throws NoSuchElementException if mono does not emit any value
 */
@JvmName("awaitVoid")
suspend fun Mono<Void>.await(): Unit = awaitComplete()

/**
 * Creates a coroutine that awaits for the result from this mono and returns it's future result as a [Deferred].
 */
fun <T : Any> Mono<T>.async(
    scope: CoroutineScope = botScope,
    context: CoroutineContext = EmptyCoroutineContext
): Deferred<T> = scope.async(context) { await() }

/**
 * Creates a coroutine that awaits for the result or completion signal from this mono and returns it's
 * future result as a [Deferred].
 */
fun <T : Any> Mono<T>.nullableAsync(
    scope: CoroutineScope = botScope,
    context: CoroutineContext = EmptyCoroutineContext
): Deferred<T?> = scope.async(context) { awaitNullable() }

/**
 * Creates a coroutine that awaits for the completion signal from this mono and returns it's future
 * result as a [Deferred].
 */
fun Mono<*>.completeAsync(
    scope: CoroutineScope = botScope,
    context: CoroutineContext = EmptyCoroutineContext
): Deferred<Unit> = scope.async(context) { awaitComplete() }

/**
 * Creates a coroutine that awaits for the completion signal from this mono and returns it's future
 * result as a [Deferred].
 */
@JvmName("asyncVoid")
fun Mono<Void>.async(
    scope: CoroutineScope = botScope,
    context: CoroutineContext = EmptyCoroutineContext
): Deferred<Unit> = completeAsync(scope)

