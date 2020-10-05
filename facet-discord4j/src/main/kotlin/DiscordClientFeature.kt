package io.facet.discord

import discord4j.core.*
import io.facet.core.*

abstract class DiscordClientFeature<out TConfig : Any, TFeature : Any>(
    keyName: String,
    requiredFeatures: List<DiscordClientFeature<*, *>> = emptyList()
) : Feature<GatewayDiscordClient, TConfig, TFeature>(keyName, requiredFeatures)
