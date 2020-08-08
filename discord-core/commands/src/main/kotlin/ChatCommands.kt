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

    private val cache = ConcurrentHashMap<String, ParseResults<ChatCommandSource>>()

    private val listener = listener<MessageCreateEvent> {
        // if user is bot, dont continue
        if (message.author.map { it.isBot }.orElse(true))
            return@listener

        val prefix = commandPrefixFor(guildId)

        // make sure message starts with the command prefix for this guild
        if (message.content.startsWith(prefix).not())
            return@listener

        val content = message.content.drop(prefix.length).trim()
        val parseResults: ParseResults<ChatCommandSource> = cache[content] ?: dispatcher.parse(
            content,
            ChatCommandSource(this, content, prefix)
        )//.also { cache[content] = it } TODO: Fix caching

        if (parseResults.exceptions.isNotEmpty())
            return@listener // TODO user feedback / help

        val aliasUsed = parseResults.reader.string.split(" ").first()
        val commandUsed = commandMap[aliasUsed] ?: error("Could not find registered command: \"${aliasUsed}\"")
        val isGuild = guildId.isPresent

        when (commandUsed.scope) {
            Scope.ALL -> dispatcher.executeSuspend(parseResults)
            Scope.GUILD -> if (isGuild) dispatcher.executeSuspend(parseResults)
            Scope.PRIVATE -> if (!isGuild) dispatcher.executeSuspend(parseResults)
        }

        client.eventDispatcher.publish(
            CommandExecutedEvent(client, shardInfo, commandUsed, parseResults.context.source)
        )
    }

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

                client.register(feature.listener)
            }
        }
    }
}
