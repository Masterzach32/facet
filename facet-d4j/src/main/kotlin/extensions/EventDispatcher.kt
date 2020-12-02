package io.facet.discord.extensions

import discord4j.core.event.*
import discord4j.core.event.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*
import reactor.core.publisher.*

inline fun <reified E : Event> EventDispatcher.on(): Flux<E> = on(E::class.java)

inline fun <reified E : Event> EventDispatcher.flowOf(): Flow<E> = on<E>().asFlow()