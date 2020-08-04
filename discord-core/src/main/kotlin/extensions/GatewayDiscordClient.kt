package io.facet.discord.extensions

import discord4j.core.*
import discord4j.core.event.domain.*
import io.facet.core.*
import io.facet.discord.*
import io.facet.discord.event.*
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.*
import kotlinx.coroutines.reactor.*
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
 * listeners are registered. If applicable, the feature can be optionally configured using the config
 * enclosure.
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
inline fun <reified E : Event> GatewayDiscordClient.listen(): Flux<E> = on(E::class.java)

private val eventScope = CoroutineScope(Job())

fun <E : Event> GatewayDiscordClient.register(listener: Listener<E>, scope: CoroutineScope = eventScope) {
    scope.async {
        on(listener.type.java).flatMap { event ->
            mono {
                try {
                    listener.on(event)
                } catch (e: Throwable) {
                    println("error occurred while processing event: ${listener.type.simpleName}")
                    e.printStackTrace()
                }
            }
        }.awaitLast()
    }
}
