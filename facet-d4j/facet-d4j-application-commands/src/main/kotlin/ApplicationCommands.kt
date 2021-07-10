package io.facet.discord.appcommands

import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.event.domain.interaction.*
import discord4j.discordjson.json.*
import discord4j.rest.*
import discord4j.rest.service.*
import io.facet.discord.*
import io.facet.discord.event.*
import io.facet.discord.extensions.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
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

    private val _commandMap = ConcurrentHashMap<Snowflake, ApplicationCommand<SlashCommandContext>>()

    /**
     * Commands that have been registered with this feature.
     */
    @Suppress("UNCHECKED_CAST")
    val commands: MutableSet<ApplicationCommand<SlashCommandContext>> =
        config.commands as MutableSet<ApplicationCommand<SlashCommandContext>>

    /**
     * Lookup map for the command object given it's unique ID.
     */
    val commandMap: Map<Snowflake, ApplicationCommand<SlashCommandContext>>
        get() = _commandMap

    /**
     * All global commands.
     */
    val globalCommands: List<ApplicationCommand<SlashCommandContext>>
        get() = commands.filter { it is GlobalApplicationCommand || it is GlobalGuildApplicationCommand }

    /**
     * All guild commands.
     */
    val guildCommands: List<GuildApplicationCommand>
        get() = commands.filterIsInstance<GuildApplicationCommand>()

    suspend fun updateCommands() {
        val globalCommandNames = globalCommands.map { it.request.name() }
        val registeredGlobalCommands: List<ApplicationCommandData> =
            service.getGlobalApplicationCommands(applicationId).await()

        // delete commands no longer in use
        registeredGlobalCommands
            .filter { it.name() !in globalCommandNames }
            .onEach { logger.info("Deleting unused application command: ${it.name()}") }
            .forEach { service.deleteGlobalApplicationCommand(applicationId, it.id().toLong()).await() }

        // create, update, or leave commands in use
        globalCommands.forEach { command ->
            val registeredCommand = registeredGlobalCommands.firstOrNull { it.name() == command.request.name() }
            logger.debug(command.request.toString())
            logger.debug(registeredCommand.toString())

            val commandId: String = when {
                // upsert command
                registeredCommand == null || isUpsertRequired(command.request, registeredCommand) -> {
                    logger.info("Pushing global application command: ${command.request.name()}")
                    service.createGlobalApplicationCommand(applicationId, command.request).await().id()
                }
                // no action necessary
                else -> registeredCommand.id()
            }

            _commandMap[commandId.toSnowflake()] = command
        }

        guildCommands
            .map { it to service.createGuildApplicationCommand(applicationId, it.guildId.asLong(), it.request).await() }
            .forEach { (command, data) ->
                _commandMap[data.id().toSnowflake()] = command as ApplicationCommand<SlashCommandContext>
            }
    }

    /**
     * Compares the application command request with the application command data.
     * If there is a difference, returns true.
     */
    private fun isUpsertRequired(request: ApplicationCommandRequest, actual: ApplicationCommandData): Boolean =
        !(request.name() == actual.name() && request.description() == actual.description() &&
            request.defaultPermission() == actual.defaultPermission() &&
            request.options() == actual.options())

    class Config {
        internal val commands = mutableSetOf<ApplicationCommand<*>>()

        var commandConcurrency: Int = Runtime.getRuntime().availableProcessors().coerceAtLeast(4)

        fun registerCommand(command: ApplicationCommand<*>) {
            commands.add(command)
        }

        fun registerCommand(vararg commands: ApplicationCommand<*>) = commands.forEach { registerCommand(it) }
    }

    companion object : GatewayFeature<Config, ApplicationCommands>("applicationCommands") {

        override suspend fun GatewayDiscordClient.install(
            scope: CoroutineScope,
            configuration: Config.() -> Unit
        ): ApplicationCommands {
            val config = Config().apply(configuration)
            return ApplicationCommands(config, restClient).also { feature ->
                scope.launch { feature.updateCommands() }

                actorListener<SlashCommandEvent>(scope) {
                    val eventsToProcess = Channel<SlashCommandEvent>()
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
            eventsToProcess: ReceiveChannel<SlashCommandEvent>
        ) = launch {
            val logger = LoggerFactory.getLogger("ApplicationCommandWorker#$index")
            for (event in eventsToProcess) {
                try {
                    processInteraction(feature, event.commandId, logger, event)
                } catch (e: Throwable) {
                    logger.error("Exception thrown while processing command:", e)
                }
            }
        }

        private suspend fun processInteraction(
            feature: ApplicationCommands,
            commandId: Snowflake,
            logger: Logger,
            event: SlashCommandEvent
        ) {
            feature.commandMap[commandId]?.also { command ->
                if (command is PermissibleApplicationCommand && !command.hasPermission(
                        event.interaction.user,
                        event.interaction.guild.awaitNullable()
                    )
                )
                    return event.reply(":no_entry_sign: You don't have permission to use that command in this server.")
                        .withEphemeral(true)
                        .await()

                val context: SlashCommandContext = when (command) {
                    is GlobalApplicationCommand -> GlobalSlashCommandContext(event)
                    is GuildApplicationCommand -> GuildSlashCommandContext(event)
                    is GlobalGuildApplicationCommand -> {
                        if (event.interaction.guildId.isPresent)
                            GuildSlashCommandContext(event)
                        else
                            return event.reply("This command is not usable within DMs.").withEphemeral(true).await()
                    }
                }

                command.run { context.execute() }
            } ?: logger.warn("Could not find command with ID ${event.commandId}, name ${event.commandName}.")
        }
    }
}