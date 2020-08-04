package io.facet.discord.extensions

import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.*
import reactor.core.publisher.*

/**
 * Extension to convert a nullable type of [T] to a [Mono] that emits the supplied
 * element when present, or complete if null.
 */
fun <T : Any> T?.toMonoOrEmpty(): Mono<T> = Mono.justOrEmpty(this)

suspend fun <T> Mono<T>.await(): T = awaitSingle()

suspend fun <T> Mono<T>.awaitNullable(): T? = awaitFirstOrNull()

@JvmName("awaitVoid")
suspend fun Mono<Void>.await(): Unit = awaitUnit()

suspend fun Mono<*>.awaitUnit(): Unit = Unit.also { awaitFirstOrNull() }

suspend fun <T> Mono<T>.awaitAsync(): Deferred<T> = coroutineScope {
    async { await() }
}

suspend fun <T> Mono<T>.awaitNullableAsync(): Deferred<T?> = coroutineScope {
    async { awaitNullable() }
}

@JvmName("awaitVoidAsync")
suspend fun Mono<Void>.awaitAsync(): Unit = awaitUnitAsync()

suspend fun Mono<*>.awaitUnitAsync(): Unit = coroutineScope {
    async { await() }
}.let { Unit }
