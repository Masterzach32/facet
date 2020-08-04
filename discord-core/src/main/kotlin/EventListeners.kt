package io.facet.discord

import discord4j.core.*
import io.facet.core.*
import io.facet.discord.event.*

class EventListeners {

    private val mutableListeners: MutableList<Listener<*>> = mutableListOf()

    val listeners: List<Listener<*>> = mutableListeners

    fun registerListener(listener: Listener<*>) {
        mutableListeners.add(listener)
    }

    @Deprecated("Use GatewayDiscordClient.register().", level = DeprecationLevel.ERROR)
    companion object : DiscordClientFeature<EmptyConfig, EventListeners>("eventListeners") {

        override fun install(client: GatewayDiscordClient, configuration: EmptyConfig.() -> Unit): EventListeners {
            return EventListeners().also { feature ->

            }
        }
    }
}
