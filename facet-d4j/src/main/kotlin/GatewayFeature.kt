package io.facet.discord

import discord4j.core.*
import io.facet.core.*

public abstract class GatewayFeature<out TConfig : Any, TFeature : Any>(
    keyName: String,
    requiredFeatures: List<Feature<*, *, *>> = emptyList()
) : Feature<GatewayDiscordClient, TConfig, TFeature>(keyName, requiredFeatures)
