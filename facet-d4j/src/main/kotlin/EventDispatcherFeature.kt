package io.facet.discord

import discord4j.core.event.*
import io.facet.core.*

abstract class EventDispatcherFeature<out TConfig : Any, TFeature : Any>(
    keyName: String,
    requiredFeatures: List<Feature<*, *, *>> = emptyList()
) : Feature<EventDispatcher, TConfig, TFeature>(keyName, requiredFeatures)
