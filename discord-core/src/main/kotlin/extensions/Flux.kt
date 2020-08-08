package io.facet.discord.extensions

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*
import reactor.core.publisher.*

suspend fun <T : Any> Flux<T>.await() = asFlow().toList()

@JvmName("awaitVoid")
suspend fun Flux<Void>.await() = asFlow().collect()
