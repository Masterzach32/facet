package io.facet.discord.extensions

import discord4j.core.*
import discord4j.core.event.*
import discord4j.core.event.domain.*
import io.facet.core.*
import io.facet.discord.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*
import reactor.core.publisher.*


/**
 * Gets the currently installed [Feature] instance, if present.
 */
@Suppress("UNCHECKED_CAST")
fun <F : Any> EventDispatcher.featureOrNull(
    feature: EventDispatcherFeature<*, F>
): F? = Features[feature.key]?.let { it as F }

/**
 * Gets the currently installed [Feature] instance, if present. Throws an IllegalStateException if the
 * requested feature is not installed.
 */
fun <F : Any> EventDispatcher.feature(
    feature: EventDispatcherFeature<*, F>
): F = featureOrNull(feature)
    ?: error("Feature with key ${feature.key} has not been installed into this DiscordClient instance!")

/**
 * Installs a [Feature] into the [DiscordClient]. The feature is immediately set up, and any event
 * listeners are registered. If applicable, the feature can be configured using the config block.
 */
@ObsoleteCoroutinesApi
suspend fun <C : Any> EventDispatcher.install(
    scope: CoroutineScope,
    feature: EventDispatcherFeature<C, *>,
    config: C.() -> Unit = {}
) {
    feature.checkRequiredFeatures()
    Features[feature.key] = with(feature) { install(scope, config) }
}

/**
 * Helper function to make listening to discord events shorter.
 */
inline fun <reified E : Event> EventDispatcher.on(): Flux<E> = on(E::class.java)

/**
 * Returns a [Flow] of the specified event type.
 */
inline fun <reified E : Event> EventDispatcher.flowOf(): Flow<E> = on<E>().asFlow()