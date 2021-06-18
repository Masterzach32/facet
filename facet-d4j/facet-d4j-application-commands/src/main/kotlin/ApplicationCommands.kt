package io.facet.discord.appcommands

import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.event.domain.*
import discord4j.rest.*
import discord4j.rest.service.*
import io.facet.discord.*
import io.facet.discord.event.*
import io.facet.discord.extensions.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*
import org.slf4j.*
import java.util.concurrent.*

/*
 * facet - Created on 6/5/2021
 * Author: Zach Kozar
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at the project root.
 */

/**
 * @author Zach Kozar
 * @version 6/5/2021
 */
class ApplicationCommands(config: Config, restClient: RestClient) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val service: ApplicationService = restClient.applicationService
    private val applicationId: Long = restClient.applicationId.block()!!

    private val _commandMap = ConcurrentHashMap<Snowflake, ApplicationCommand<InteractionContext>>()

    /**
     * Commands that have been registered with this feature.
     */
    @Suppress("UNCHECKED_CAST")
    val commands: MutableSet<ApplicationCommand<InteractionContext>> =
        config.commands as MutableSet<ApplicationCommand<InteractionContext>>

    /**
     * Lookup map for the command object given it's unique ID.
     */
    val commandMap: Map<Snowflake, ApplicationCommand<InteractionContext>>
        get() = _commandMap

    /**
     * All global commands.
     */
    val globalCommands: List<ApplicationCommand<InteractionContext>>
        get() = commands.filter { it is GlobalApplicationCommand || it is GlobalGuildApplicationCommand }

    /**
     * All guild commands.
     */
    val guildCommands: List<GuildApplicationCommand>
        get() = commands.filterIsInstance<GuildApplicationCommand>()

    suspend fun updateCommands() {
        val globalCommandNames = globalCommands.map { it.request.name() }
        service.getGlobalApplicationCommands(applicationId).asFlow()
            .filter { it.name() !in globalCommandNames }
            .collect { service.deleteGlobalApplicationCommand(applicationId, it.id().toLong()).await() }

        globalCommands.asFlow()
            .map { it to service.createGlobalApplicationCommand(applicationId, it.request).await() }
            .collect { (command, data) ->
                _commandMap[data.id().toSnowflake()] = command
            }

        guildCommands.asFlow()
            .map { it to service.createGuildApplicationCommand(applicationId, it.guildId.asLong(), it.request).await() }
            .collect { (command, data) ->
                _commandMap[data.id().toSnowflake()] = command as ApplicationCommand<InteractionContext>
            }
    }

    class Config {
        internal val commands = mutableSetOf<ApplicationCommand<*>>()

        var commandConcurrency: Int = Runtime.getRuntime().availableProcessors().coerceAtLeast(4)

        fun registerCommand(command: ApplicationCommand<*>) {
            commands.add(command)
        }

        fun registerCommand(vararg commands: ApplicationCommand<*>) = commands.forEach { registerCommand(it) }
    }

    companion object : GatewayFeature<Config, ApplicationCommands>("applicationCommands") {

        override fun GatewayDiscordClient.install(
            scope: CoroutineScope,
            configuration: Config.() -> Unit
        ): ApplicationCommands {
            val config = Config().apply(configuration)
            return ApplicationCommands(config, restClient).also { feature ->
                scope.launch { feature.updateCommands() }

                actorListener<InteractionCreateEvent>(scope) {
                    val eventsToProcess = Channel<InteractionCreateEvent>()
                    for (i in 0 until config.commandConcurrency)
                        commandWorker(feature, i, eventsToProcess)

                    for (event in channel)
                        eventsToProcess.send(event)
                }
            }
        }

        private fun CoroutineScope.commandWorker(
            feature: ApplicationCommands,
            index: Int,
            eventsToProcess: ReceiveChannel<InteractionCreateEvent>
        ) = launch {
            val logger = LoggerFactory.getLogger("ApplicationCommandWorker#$index")
            for (event in eventsToProcess) {
                try {
                    processInteraction(feature, logger, event)
                } catch (e: Throwable) {
                    logger.error("Exception thrown while processing command:", e)
                }
            }
        }

        private suspend fun processInteraction(
            feature: ApplicationCommands,
            logger: Logger,
            event: InteractionCreateEvent
        ) {
            feature.commandMap[event.commandId]?.also { command ->
                if (command is PermissibleApplicationCommand && !command.hasPermission(
                        event.interaction.user,
                        event.interaction.guild.awaitNullable()
                    )
                )
                    return event.replyEphemeral(
                        "You don't have permission to use that command in this server. Ask a server " +
                            "admin if you think that shouldn't be the case."
                    ).await()

                val context: InteractionContext = when (command) {
                    is GlobalApplicationCommand -> GlobalInteractionContext(event)
                    is GuildApplicationCommand -> GuildInteractionContext(event)
                    is GlobalGuildApplicationCommand -> {
                        if (event.interaction.guildId.isPresent)
                            GuildInteractionContext(event)
                        else
                            return event.reply("This command is not usable within DMs.").await()
                    }
                }

                context.apply { command.apply { execute() } }
            } ?: logger.warn("Could not find command with ID ${event.commandId}, name ${event.commandName}.")
        }
    }
}
