/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.facet.core.features

import discord4j.common.util.Snowflake
import discord4j.common.util.TimestampFormat
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.Guild
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.interaction.MessageInteractionEvent
import discord4j.core.event.domain.interaction.UserInteractionEvent
import discord4j.discordjson.json.ApplicationCommandData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.RestClient
import discord4j.rest.service.ApplicationService
import io.facet.commands.*
import io.facet.common.*
import io.facet.core.BotScope
import io.facet.core.GatewayFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Plugin for using "slash" commands
 */
@OptIn(ObsoleteCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
public class ApplicationCommands(config: Config, restClient: RestClient, private val applicationId: Long) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val service: ApplicationService = restClient.applicationService

    private val _commandMap = ConcurrentHashMap<Snowflake, ApplicationCommand<ApplicationCommandContext>>()

    /**
     * Commands that have been registered with this feature.
     */
    @Suppress("UNCHECKED_CAST")
    public val commands: MutableSet<ApplicationCommand<ApplicationCommandContext>> =
        config.commands as MutableSet<ApplicationCommand<ApplicationCommandContext>>

    /**
     * Lookup map for the command object given it's unique ID.
     */
    public val commandMap: Map<Snowflake, ApplicationCommand<ApplicationCommandContext>>
        get() = _commandMap

    /**
     * All global commands.
     */
    public val globalCommands: List<ApplicationCommand<ApplicationCommandContext>>
        get() = commands.filter { it is GlobalApplicationCommand || it is GlobalGuildApplicationCommand }

    /**
     * All guild commands.
     */
    public val guildCommands: List<GuildApplicationCommand<*>>
        get() = commands.filterIsInstance<GuildApplicationCommand<*>>()

    public suspend fun updateCommands(guilds: Flow<Guild>) {
        val globalCommandNames = globalCommands.map { it.request.name() }
        val registeredGlobalCommands: List<ApplicationCommandData> =
            service.getGlobalApplicationCommands(applicationId).await()

        // delete commands no longer in use
        registeredGlobalCommands
            .filter { it.name() !in globalCommandNames }
            .onEach { logger.info("Deleting unused global application command: ${it.name()}") }
            .forEach { service.deleteGlobalApplicationCommand(applicationId, it.id().toLong()).await() }

        // create, update, or leave commands in use
        globalCommands.forEach { command ->
            val registeredCommand = registeredGlobalCommands.firstOrNull { it.name() == command.request.name() }

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

        guilds.collect { guild ->
            val guildCommands = guildCommands.filter { it.guildId == guild.id }

            val guildCommandNames = guildCommands.map { it.request.name() }
            val registeredCommands = service.getGuildApplicationCommands(applicationId, guild.id.asLong()).asFlow().toList()

            registeredCommands
                .filter { it.name() !in guildCommandNames }
                .onEach { logger.info("Deleting unused guild application command: ${it.name()} for guildId ${guild.id}") }
                .forEach { service.deleteGuildApplicationCommand(applicationId, guild.id.asLong(), it.id().toLong()).await() }

            guildCommands.forEach { command ->
                val registeredCommand = registeredCommands.firstOrNull { it.name() == command.request.name() }

                val commandId: String = when {
                    // upsert command
                    registeredCommand == null || isUpsertRequired(command.request, registeredCommand) -> {
                        logger.info("Pushing guild application command: ${command.request.name()} for guildId ${guild.id}")
                        service.createGuildApplicationCommand(applicationId, guild.id.asLong(), command.request).await().id()
                    }
                    // no action necessary
                    else -> registeredCommand.id()
                }

                _commandMap[commandId.toSnowflake()] = command as ApplicationCommand<ApplicationCommandContext>
            }
        }
    }

//    private suspend fun updateCommands(
//        commands: List<ApplicationCommand<*>>,
//        registeredCommands: Flow<ApplicationCommandData>,
//        updateCommand: suspend (request: ApplicationCommandRequest, guildId: Long?) -> ApplicationCommandData,
//        deleteCommand: suspend (id: Long, guildId: Long?) -> Unit,
//        guildId: Long? = null
//    ) {
//        val commandNames = commands.map { it.request.name() }
//
//        coroutineScope {
//            registeredCommands
//                .filter { it.name() !in commandNames }
//                .onEach { logger.info("Deleting unused command: $it") }
//                .onEach { deleteCommand(it.id().toLong(), guildId) }
//                .launchIn(this)
//
//            commands.forEach { command ->
//                val registeredCommand = registeredCommands.firstOrNull { it.name() == command.request.name() }
//
//                val commandId: String = when {
//                    // upsert command
//                    registeredCommand == null || isUpsertRequired(command.request, registeredCommand) -> {
//                        logger.info("Pushing global application command: ${command.request.name()}")
//                        updateCommand(command.request, guildId).id()
//                    }
//                    // no action necessary
//                    else -> registeredCommand.id()
//                }
//
//                _commandMap[commandId.toSnowflake()] = command as ApplicationCommand<ApplicationCommandContext<*>>
//            }
//        }
//    }

    /**
     * Compares the application command request with the application command data.
     * If there is a difference, returns true.
     */
    private fun isUpsertRequired(request: ApplicationCommandRequest, actual: ApplicationCommandData): Boolean =
        !(request.name() == actual.name() && request.description().unwrap() == actual.description() &&
            request.defaultPermission() == actual.defaultPermission() &&
            request.options() == actual.options())

    public class Config {
        internal val commands = mutableSetOf<ApplicationCommand<*>>()

        public var commandConcurrency: Int = Runtime.getRuntime().availableProcessors().coerceAtLeast(4)

        public fun registerCommand(command: ApplicationCommand<*>) {
            commands.add(command)
        }

        public fun registerCommands(vararg commands: ApplicationCommand<*>): Unit = registerCommand(*commands)

        public fun registerCommand(vararg commands: ApplicationCommand<*>): Unit =
            commands.forEach { registerCommand(it) }
    }

    public companion object : GatewayFeature<Config, ApplicationCommands>("applicationCommands") {

        override suspend fun GatewayDiscordClient.install(
            scope: CoroutineScope,
            configuration: Config.() -> Unit
        ): ApplicationCommands {
            val config = Config().apply(configuration)
            return ApplicationCommands(config, restClient, restClient.applicationId.await()).also { feature ->
                scope.launch { feature.updateCommands(guilds.asFlow()) }

                actorListener<ApplicationCommandInteractionEvent>(scope) {
                    val eventsToProcess = Channel<ApplicationCommandInteractionEvent>()
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
            eventsToProcess: ReceiveChannel<ApplicationCommandInteractionEvent>
        ) = launch {
            val logger = LoggerFactory.getLogger("ApplicationCommandWorker#$index")
            for (event in eventsToProcess) {
                try {
                    processInteraction(feature, event.commandId, logger, event)
                } catch (e: Throwable) {
                    logger.error("Exception thrown while processing command:", e)
                }
            }
            TimestampFormat.RELATIVE_TIME
        }

        private suspend fun processInteraction(
            feature: ApplicationCommands,
            commandId: Snowflake,
            logger: Logger,
            event: ApplicationCommandInteractionEvent
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

                val context: ApplicationCommandContext = when (event) {
                    is ChatInputInteractionEvent -> when (command) {
                        is GlobalSlashCommand -> GlobalSlashCommandContext(event, BotScope)
                        is GuildSlashCommand -> GuildSlashCommandContext(event, BotScope)
                        is GlobalGuildSlashCommand -> {
                            if (event.interaction.guildId.isPresent)
                                GuildSlashCommandContext(event, BotScope)
                            else
                                return event.reply("This command is not usable within DMs.").withEphemeral(true).await()
                        }
                        else -> error("Unknown CHAT_INPUT command received: ${event.commandName}")
                    }
                    is MessageInteractionEvent -> when (command) {
                        is GlobalMessageCommand -> GlobalMessageCommandContext(event, BotScope)
                        is GuildMessageCommand -> GuildMessageCommandContext(event, BotScope)
                        is GlobalGuildMessageCommand -> {
                            if (event.interaction.guildId.isPresent)
                                GuildMessageCommandContext(event, BotScope)
                            else
                                return event.reply("This command is not usable within DMs.").withEphemeral(true).await()
                        }
                        else -> error("Unknown MESSAGE command received: ${event.commandName}")
                    }
                    is UserInteractionEvent -> when (command) {
                        is GlobalUserCommand -> GlobalUserCommandContext(event, BotScope)
                        is GuildUserCommand -> GuildUserCommandContext(event, BotScope)
                        is GlobalGuildUserCommand -> {
                            if (event.interaction.guildId.isPresent)
                                GuildUserCommandContext(event, BotScope)
                            else
                                return event.reply("This command is not usable within DMs.").withEphemeral(true).await()
                        }
                        else -> error("Unknown USER command received: ${event.commandName}")
                    }
                    else -> error("Unknown application command received: ${event.commandName}")
                }

                command.run { context.execute() }
            } ?: logger.warn("Could not find command with ID ${event.commandId}, name ${event.commandName}.")
        }
    }
}
