package io.facet.discord.extensions

import discord4j.core.*
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
fun <TFeature : Any> GatewayDiscordClient.featureOrNull(
    feature: Feature<*, *, TFeature>
): TFeature? = Features[feature.key]?.let { it as TFeature }

/**
 * Gets the currently installed [Feature] instance, if present. Throws an IllegalStateException if the
 * requested feature is not installed.
 */
fun <TFeature : Any> GatewayDiscordClient.feature(
    feature: Feature<*, *, TFeature>
): TFeature = featureOrNull(feature)
    ?: error("Feature with key ${feature.key} has not been installed into this DiscordClient instance!")

/**
 * Installs a [Feature] into the [DiscordClient]. The feature is immediately set up, and any event
 * listeners are registered. If applicable, the feature can be configured using the config block.
 */
@ObsoleteCoroutinesApi
suspend fun <TConfiguration : Any> GatewayDiscordClient.install(
    scope: CoroutineScope,
    feature: GatewayFeature<TConfiguration, *>,
    config: TConfiguration.() -> Unit = {}
) {
    feature.checkRequiredFeatures()
    Features[feature.key] = with(feature) { install(scope, config) }
}

/**
 * Helper function to make listening to discord events shorter.
 */
inline fun <reified E : Event> GatewayDiscordClient.on(): Flux<E> = on(E::class.java)

inline fun <reified E : Event> GatewayDiscordClient.flowOf(): Flow<E> = on<E>().asFlow()

