package io.facet.discord.extensions

import kotlinx.coroutines.*
import reactor.core.publisher.*

suspend fun <T> Flux<T>.await(): List<T> = collectList().await()

@JvmName("awaitVoid")
suspend fun Flux<Void>.await(): Unit = Unit.also { collectList().await() }

suspend fun <T> Flux<T>.awaitAsync(): Deferred<List<T>> = coroutineScope {
    async { await() }
}

@JvmName("awaitVoidAsync")
suspend fun Flux<Void>.awaitAsync(): Unit = awaitUnitAsync()

suspend fun Flux<*>.awaitUnitAsync(): Unit = coroutineScope {
    async { await() }
}.let { Unit }
