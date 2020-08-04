package io.facet.discord

import discord4j.core.*
import discord4j.core.event.domain.*
import io.facet.core.*
import io.facet.discord.event.*

abstract class DiscordClientFeature<out TConfig : Any, TFeature : Any>(
    keyName: String,
    requiredFeatures: List<DiscordClientFeature<*, *>> = emptyList()
) : Feature<GatewayDiscordClient, TConfig, TFeature>(keyName, requiredFeatures)
