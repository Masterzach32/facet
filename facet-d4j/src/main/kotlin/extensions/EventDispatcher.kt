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
fun <TFeature : Any> EventDispatcher.featureOrNull(
    feature: EventDispatcherFeature<*, TFeature>
): TFeature? = Features[feature.key]?.let { it as TFeature }

/**
 * Gets the currently installed [Feature] instance, if present. Throws an IllegalStateException if the
 * requested feature is not installed.
 */
fun <TFeature : Any> EventDispatcher.feature(
    feature: EventDispatcherFeature<*, TFeature>
): TFeature = featureOrNull(feature)
    ?: error("Feature with key ${feature.key} has not been installed into this DiscordClient instance!")

/**
 * Installs a [Feature] into the [DiscordClient]. The feature is immediately set up, and any event
 * listeners are registered. If applicable, the feature can be configured using the config block.
 */
@Deprecated("Use install with CoroutineScope parameter.")
suspend fun <TConfiguration : Any> EventDispatcher.install(
    feature: EventDispatcherFeature<TConfiguration, *>,
    config: TConfiguration.() -> Unit = {}
) {
    feature.checkRequiredFeatures()
    Features[feature.key] = with(feature) { install(BotScope, config) }
}

/**
 * Installs a [Feature] into the [DiscordClient]. The feature is immediately set up, and any event
 * listeners are registered. If applicable, the feature can be configured using the config block.
 */
@ObsoleteCoroutinesApi
suspend fun <TConfiguration : Any> EventDispatcher.install(
    scope: CoroutineScope,
    feature: EventDispatcherFeature<TConfiguration, *>,
    config: TConfiguration.() -> Unit = {}
) {
    feature.checkRequiredFeatures()
    Features[feature.key] = with(feature) { install(scope, config) }
}

inline fun <reified E : Event> EventDispatcher.on(): Flux<E> = on(E::class.java)

inline fun <reified E : Event> EventDispatcher.flowOf(): Flow<E> = on<E>().asFlow()