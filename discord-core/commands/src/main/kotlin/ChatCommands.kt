package io.facet.discord.commands

import com.mojang.brigadier.*
import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.event.domain.message.*
import io.facet.discord.*
import io.facet.discord.commands.events.*
import io.facet.discord.commands.extensions.*
import io.facet.discord.event.*
import io.facet.discord.extensions.*
import java.util.*
import java.util.concurrent.*

/**
 * Instance of the [ChatCommands] feature.
 */
class ChatCommands(config: Config, private val client: GatewayDiscordClient) {

    private val mutableCommands = config.commands.toMutableSet()

    /**
     * Commands that have been registered with this feature.
     */
    val commands: Set<ChatCommand>
        get() = mutableCommands.toSet()

    /**
     * Lookup map for the command object for a given alias.
     */
    val commandMap: Map<String, ChatCommand>
        get() = commands
            .flatMap(ChatCommand::aliases)
            .map { alias -> alias to commands.first { it.aliases.contains(alias) } }
            .toMap()

    /**
     * Command dispatcher instance
     */
    val dispatcher = CommandDispatcher<ChatCommandSource>()

    /**
     * Function that gets the command prefix for the specified guild.
     */
    val commandPrefixFor = config.commandPrefix

    fun registerCommand(command: ChatCommand): Boolean {
        return commands.flatMap { it.aliases }
            .firstOrNull { command.aliases.contains(it) } // if alias is found, will return alias, else null
            ?.let { duplicate -> error("Could not register command ${command.name} due to duplicate alias: $duplicate") }
            ?: mutableCommands.add(command).also { added -> if (added) command.register(client, dispatcher) }
    }

    fun registerCommands(vararg commands: ChatCommand) = commands.forEach { registerCommand(it) }

    class Config {
        internal val commands = mutableSetOf<ChatCommand>()
        internal lateinit var commandPrefix: suspend (guildId: Optional<Snowflake>) -> String

        fun commandPrefix(block: suspend (guildId: Optional<Snowflake>) -> String) {
            commandPrefix = block
        }
        
        fun registerCommand(command: ChatCommand): Boolean {
            return commands.flatMap { it.aliases }
                .firstOrNull { command.aliases.contains(it) } // if duplicate alias is found, will return alias, else null
                ?.let { duplicate -> error("Could not register command ${command.name} due to duplicate alias: $duplicate") }
                ?: commands.add(command)
        }

        fun registerCommands(vararg commands: ChatCommand) = commands.forEach { registerCommand(it) }
    }

    /**
     * Adds the functionality to the [DiscordClient] to easily listen to and parse user commands. Must be configured
     * with command prefix and commands.
     */
    companion object : DiscordClientFeature<Config, ChatCommands>("commands") {

        override fun install(client: GatewayDiscordClient, configuration: Config.() -> Unit): ChatCommands {
            return ChatCommands(Config().apply(configuration), client).also { feature ->
                feature.commands.forEach { command ->
                    command.register(client, feature.dispatcher)
                }

                client.register(ChatCommandListener(feature))
            }
        }
    }
}
