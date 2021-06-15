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
    private val applicationService: ApplicationService = restClient.applicationService
    private val applicationId: Long = restClient.applicationId.block()!!

    private val _commandMap = ConcurrentHashMap<Snowflake, ApplicationCommand<*>>()

    /**
     * Commands that have been registered with this feature.
     */
    val commands: MutableSet<ApplicationCommand<*>> = config.commands

    /**
     * Lookup map for the command object given it's unique ID
     */
    val commandMap: Map<Snowflake, ApplicationCommand<*>>
        get() = _commandMap

    /**
     * All global commands.
     */
    val globalCommands: List<ApplicationCommand<*>>
        get() = commands.filter { it is GlobalApplicationCommand || it is GlobalGuildApplicationCommand }

    /**
     * All guild commands.
     */
    val guildCommands: List<GuildApplicationCommand>
        get() = commands.filterIsInstance<GuildApplicationCommand>()

    suspend fun updateCommands() {
        val globalCommandNames = globalCommands.map { it.request.name() }
        applicationService.getGlobalApplicationCommands(applicationId).asFlow()
            .collect { registeredCommand ->
                when {
                    registeredCommand.name() in globalCommandNames -> {}
                }
            }

        globalCommands.asFlow()
            .onEach { logger.info("Registering command: ${it.request}") }
            .map { it to applicationService.createGlobalApplicationCommand(applicationId, it.request).await() }
            .onEach { (_, data) -> logger.info("Command registered, (id=${data.id()}, name=${data.name()})") }
            .collect { (command, data) ->
                _commandMap[data.id().toSnowflake()] = command
            }

        guildCommands.asFlow()
            .onEach { logger.info("Registering guild command: ${it.request}") }
            .map {
                it to applicationService.createGuildApplicationCommand(applicationId, it.guildId.asLong(), it.request).await()
            }
            .onEach { (_, data) -> logger.info("Command registered, (id=${data.id()}, name=${data.name()})") }
            .collect { (command, data) ->
                _commandMap[data.id().toSnowflake()] = command
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
                    processInteraction(feature, logger, this, event)
                } catch (e: Throwable) {
                    logger.error("Exception thrown while processing command:", e)
                }
            }
        }

        private suspend fun processInteraction(
            feature: ApplicationCommands,
            logger: Logger,
            scope: CoroutineScope,
            event: InteractionCreateEvent
        ) {
            feature.commandMap[event.commandId]?.also { command ->
                if (command is PermissibleApplicationCommand && !command.hasPermission(event.interaction.user, event.interaction.guild.awaitNullable()))
                    return event.replyEphemeral("You don't have permission to use that command in this server. Ask a " +
                        "server admin if you think that shouldn't be the case.").await()

                when (command) {
                    is GlobalApplicationCommand -> {
                        GlobalInteractionContext(event, scope).apply { command.apply { execute() } }
                    }
                    is GuildApplicationCommand -> {
                        GuildInteractionContext(event, scope).apply { command.apply { execute() } }
                    }
                    is GlobalGuildApplicationCommand -> {
                        if (event.interaction.guildId.isPresent)
                            GuildInteractionContext(event, scope).apply { command.apply { execute() } }
                        else
                            return event.reply("This command is not usable within DMs.").await()
                    }
                }
            } ?: logger.warn("Could not find command with ID ${event.commandId}, name ${event.commandName}.")
        }
    }
}
