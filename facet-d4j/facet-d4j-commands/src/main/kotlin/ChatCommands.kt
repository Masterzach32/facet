package io.facet.discord.commands

import com.mojang.brigadier.*
import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.entity.channel.*
import discord4j.core.event.*
import discord4j.core.event.domain.message.*
import io.facet.core.extensions.*
import io.facet.discord.*
import io.facet.discord.commands.events.*
import io.facet.discord.commands.extensions.*
import io.facet.discord.event.*
import io.facet.discord.extensions.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.slf4j.*
import java.util.concurrent.*

/**
 * Instance of the [ChatCommands] feature.
 */
@OptIn(ObsoleteCoroutinesApi::class)
class ChatCommands(config: Config) {

    private val logger = LoggerFactory.getLogger(ChatCommands::class.java)

    private val _commands = mutableSetOf<ChatCommand>()
    private val _commandMap = mutableMapOf<String, ChatCommand>()

    /**
     * Commands that have been registered with this feature.
     */
    val commands: Set<ChatCommand>
        get() = _commands

    /**
     * Lookup map for the command object for a given alias.
     */
    val commandMap: Map<String, ChatCommand>
        get() = _commandMap

    /**
     * Command dispatcher instance
     */
    val dispatcher = CommandDispatcher<ChatCommandSource>()

    /**
     * Function that gets the command prefix for the specified guild.
     */
    val commandPrefixFor = config.commandPrefix

    init {
        if (config.useDefaultHelpCommand)
            registerCommand(Help)

        config.commands.forEach { registerCommand(it) }
    }

    private val cache = ConcurrentHashMap<String, ParseResults<ChatCommandSource>>()

    fun registerCommand(command: ChatCommand): Boolean {
        val duplicateAlias = commands
            .flatMap { it.aliases }
            .firstOrNull { command.aliases.contains(it) }

        if (duplicateAlias != null)
            error("Could not register command ${command.name} due to duplicate alias: $duplicateAlias")

        return _commands.add(command).also { added ->
            if (added) {
                command.register(dispatcher)

                command.aliases.forEach { alias ->
                    _commandMap[alias] = command
                }
            }
        }
    }

    fun registerCommands(vararg commands: ChatCommand) = commands.map { it to registerCommand(it) }.toMap()

    class Config {
        internal val commands = mutableSetOf<ChatCommand>()
        internal lateinit var commandPrefix: suspend (guildId: Snowflake?) -> String

        var commandConcurrency: Int = Runtime.getRuntime().availableProcessors().coerceAtLeast(4)

        var useDefaultHelpCommand = false

        fun commandPrefix(block: suspend (guildId: Snowflake?) -> String) {
            commandPrefix = block
        }
        
        fun registerCommand(command: ChatCommand): Boolean {
            return commands.add(command)
        }

        fun registerCommands(vararg commands: ChatCommand) = commands.forEach { registerCommand(it) }
    }

    /**
     * Adds the functionality to the [DiscordClient] to easily listen to and parse user commands. Must be configured
     * with command prefix and commands.
     */
    companion object : EventDispatcherFeature<Config, ChatCommands>("commands") {

        override suspend fun EventDispatcher.install(scope: CoroutineScope, configuration: Config.() -> Unit): ChatCommands {
            val config = Config().apply(configuration)
            return ChatCommands(config).also { feature ->
                actorListener<MessageCreateEvent>(scope) {
                    val eventsToProcess = Channel<MessageCreateEvent>()
                    for (i in 0 until config.commandConcurrency)
                        commandWorker(feature, i, eventsToProcess)

                    for (event in channel)
                        eventsToProcess.send(event)
                }
            }
        }

        private fun CoroutineScope.commandWorker(
            feature: ChatCommands,
            index: Int,
            eventsToProcess: ReceiveChannel<MessageCreateEvent>
        ) = launch {
            val logger = LoggerFactory.getLogger("CommandWorker#$index")
            for (event in eventsToProcess) {
                try {
                    processEvent(this, feature, event)
                } catch (e: Throwable) {
                    logger.warn("Exception thrown while processing command:", e)
                }
            }
        }

        private suspend fun processEvent(
            scope: CoroutineScope,
            feature: ChatCommands,
            event: MessageCreateEvent
        ) {
            // if user is bot, skip and continue to next event
            if (event.message.author.unwrap()?.isBot == true)
                return

            val prefix = feature.commandPrefixFor(event.guildId.unwrap())

            // make sure message starts with the command prefix for this guild
            if (event.message.content.startsWith(prefix).not())
                return

            val content = event.message.content.drop(prefix.length).trim()
            val parseResults: ParseResults<ChatCommandSource> = feature.cache[content] ?: feature.dispatcher.parse(
                content,
                ChatCommandSource(event, content, prefix, scope)
            )//.also { cache[content] = it } TODO: Fix caching

            if (parseResults.exceptions.isNotEmpty())
                return // TODO user feedback / help

            val aliasUsed = parseResults.reader.string.split(" ").first()
            val commandUsed = feature.commandMap[aliasUsed] ?: return
            val isGuild = event.guildId.isPresent

            if (isGuild && commandUsed.discordPermsRequired.isNotEmpty()) {
                val channel = event.message.channel.await() as GuildMessageChannel
                val ourEffectivePerms = channel.getEffectivePermissions(event.client.selfId).await()
                val userEffectivePerms = channel.getEffectivePermissions(event.member.unwrap()!!.id).await()

                if (!ourEffectivePerms.containsAll(commandUsed.discordPermsRequired) ||
                    !userEffectivePerms.containsAll(commandUsed.discordPermsRequired))
                    return // we or the user do not have the discord permissions to use this command
            }

            when (commandUsed.scope) {
                Scope.ALL -> feature.dispatcher.executeSuspend(parseResults)
                Scope.GUILD -> if (isGuild) feature.dispatcher.executeSuspend(parseResults)
                Scope.PRIVATE -> if (!isGuild) feature.dispatcher.executeSuspend(parseResults)
            }

            if (isGuild && commandUsed.scope == Scope.GUILD || !isGuild && commandUsed.scope == Scope.PRIVATE) {
                scope.launch {
                    event.client.eventDispatcher.publish(
                        CommandExecutedEvent(
                            event.client,
                            event.shardInfo,
                            commandUsed,
                            parseResults.context.source,
                            aliasUsed
                        )
                    )
                }
            }
        }
    }
}
