package io.discordcommons.core.extensions

import discord4j.core.*
import discord4j.core.event.domain.*
import io.discordcommons.core.*
import reactor.core.publisher.*

/**
 * Gets the currently installed [Feature] instance, if present.
 */
@Suppress("UNCHECKED_CAST")
fun <TConfiguration : Any, TFeature : Any> DiscordClient.featureOrNull(
        feature: Feature<TConfiguration, TFeature>
): TFeature? = Features[feature.key]?.let { it as TFeature }

/**
 * Gets the currently installed [Feature] instance, if present. Throws an IllegalStateException if the
 * requested feature is not installed.
 */
fun <TConfiguration : Any, TFeature : Any> DiscordClient.feature(
        feature: Feature<TConfiguration, TFeature>
): TFeature = featureOrNull(feature) ?: throw IllegalStateException("Feature with key ${feature.key} has not been " +
        "installed into this DiscordClient instance!")

/**
 * Installs a [Feature] into the [DiscordClient]. The feature is immediately set up, and any event listeners are
 * registered. If applicable, the feature can be optionally configured using the config enclosure.
 */
fun <TConfiguration : Any, TFeature : Any> DiscordClient.install(
        feature: Feature<TConfiguration, TFeature>,
        config: TConfiguration.() -> Unit = {}
) {
    feature.checkRequiredFeatures()
    feature.also { Features[it.key] = it.install(this, config) }
}

/**
 * Helper function to make listening to discord events shorter.
 */
inline fun <reified E : Event> DiscordClient.listen(): Flux<E> = eventDispatcher.on(E::class.java)
