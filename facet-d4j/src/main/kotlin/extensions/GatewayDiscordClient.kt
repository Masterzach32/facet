package io.facet.discord.extensions

import discord4j.core.*
import discord4j.core.event.domain.*
import io.facet.core.*
import io.facet.discord.*
import kotlinx.coroutines.*
import reactor.core.publisher.*

/**
 * Gets the currently installed [Feature] instance, if present.
 */
@Suppress("UNCHECKED_CAST")
fun <TConfiguration : Any, TFeature : Any> GatewayDiscordClient.featureOrNull(
        feature: DiscordClientFeature<TConfiguration, TFeature>
): TFeature? = Features[feature.key]?.let { it as TFeature }

/**
 * Gets the currently installed [Feature] instance, if present. Throws an IllegalStateException if the
 * requested feature is not installed.
 */
fun <TConfiguration : Any, TFeature : Any> GatewayDiscordClient.feature(
        feature: DiscordClientFeature<TConfiguration, TFeature>
): TFeature = featureOrNull(feature)
    ?: error("Feature with key ${feature.key} has not been installed into this DiscordClient instance!")

/**
 * Installs a [Feature] into the [DiscordClient]. The feature is immediately set up, and any event
 * listeners are registered. If applicable, the feature can be configured using the config block.
 */
fun <TConfiguration : Any, TFeature : Any> GatewayDiscordClient.install(
        feature: DiscordClientFeature<TConfiguration, TFeature>,
        config: TConfiguration.() -> Unit = {}
) {
    feature.checkRequiredFeatures()
    feature.also { Features[it.key] = it.install(this, config) }
}

/**
 * Helper function to make listening to discord events shorter.
 */
inline fun <reified E : Event> GatewayDiscordClient.on(): Flux<E> = on(E::class.java)

/**
 * Extension property to get the [BotScope] from the [GatewayDiscordClient] instance.
 */
@Deprecated("Use BotScope object.", ReplaceWith("BotScope"))
val GatewayDiscordClient.scope: CoroutineScope
    get() = BotScope

