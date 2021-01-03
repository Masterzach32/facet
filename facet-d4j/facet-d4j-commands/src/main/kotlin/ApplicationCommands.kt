package io.facet.discord.commands

import discord4j.core.*
import io.facet.discord.*
import kotlinx.coroutines.*

class ApplicationCommands {

    class Config

    companion object : GatewayFeature<Config, ApplicationCommands>("applicationCommands") {

        override fun GatewayDiscordClient.install(
            scope: CoroutineScope,
            configuration: Config.() -> Unit
        ): ApplicationCommands {
            TODO("Not yet implemented")
        }
    }
}
