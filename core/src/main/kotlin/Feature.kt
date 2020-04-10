package io.discordcommons.core

import discord4j.core.DiscordClient

/**
 * A feature is any code that can be installed into the [DiscordClient] to improve its functionality.
 */
abstract class Feature<out TConfiguration : Any, TFeature : Any>(
        keyName: String,
        val requiredFeatures: List<Feature<*, *>> = emptyList()
) {

    val key = AttributeKey<TFeature>(keyName)

    fun checkRequiredFeatures() = requiredFeatures
            .map { it.key }
            .filterNot { Features.containsKey(it) }
            .map { it.name }
            .let {
                if (it.isNotEmpty())
                    throw IllegalStateException("Could not install feature: ${key.name}. " +
                            "Missing required features: $it")
            }

    abstract fun install(client: DiscordClient, configuration: TConfiguration.() -> Unit): TFeature
}