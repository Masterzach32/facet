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

import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.event.domain.interaction.*
import discord4j.discordjson.json.*
import discord4j.rest.*
import discord4j.rest.service.*
import io.facet.commands.*
import io.facet.common.*
import io.facet.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.slf4j.*
import java.util.concurrent.*

/**
 * Plugin for using "slash" commands
 */
@OptIn(ObsoleteCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
public class ApplicationCommands(config: Config, restClient: RestClient) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val service: ApplicationService = restClient.applicationService
    private val applicationId: Long = restClient.applicationId.block()!!

    private val _commandMap = ConcurrentHashMap<Snowflake, ApplicationCommand<SlashCommandContext>>()

    /**
     * Commands that have been registered with this feature.
     */
    @Suppress("UNCHECKED_CAST")
    public val commands: MutableSet<ApplicationCommand<SlashCommandContext>> =
        config.commands as MutableSet<ApplicationCommand<SlashCommandContext>>

    /**
     * Lookup map for the command object given it's unique ID.
     */
    public val commandMap: Map<Snowflake, ApplicationCommand<SlashCommandContext>>
        get() = _commandMap

    /**
     * All global commands.
     */
    public val globalCommands: List<ApplicationCommand<SlashCommandContext>>
        get() = commands.filter { it is GlobalApplicationCommand || it is GlobalGuildApplicationCommand }

    /**
     * All guild commands.
     */
    public val guildCommands: List<GuildApplicationCommand>
        get() = commands.filterIsInstance<GuildApplicationCommand>()

    public suspend fun updateCommands() {
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

    public class Config {
        internal val commands = mutableSetOf<ApplicationCommand<*>>()

        public var commandConcurrency: Int = Runtime.getRuntime().availableProcessors().coerceAtLeast(4)

        public fun registerCommand(command: ApplicationCommand<*>) {
            commands.add(command)
        }

        public fun registerCommands(vararg commands: ApplicationCommand<*>): Unit = registerCommand(*commands)

        public fun registerCommand(vararg commands: ApplicationCommand<*>): Unit = commands.forEach { registerCommand(it) }
    }

    public companion object : GatewayFeature<Config, ApplicationCommands>("applicationCommands") {

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
