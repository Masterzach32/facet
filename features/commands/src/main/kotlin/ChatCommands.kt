package io.discordcommons.commands

import com.mojang.brigadier.*
import discord4j.core.*
import discord4j.core.`object`.util.*
import discord4j.core.event.domain.message.*
import io.discordcommons.core.*
import io.discordcommons.core.extensions.*
import java.util.*

/**
 * Instance of the [ChatCommands.Companion] feature.
 */
class ChatCommands(config: Config) {

    /**
     * Command dispatcher instance
     */
    val dispatcher = config.dispatcher

    /**
     * Function that gets the command prefix for the specified guild.
     */
    val commandPrefixFor = config.commandPrefix

    private val cache = mutableMapOf<String, ParseResults<MessageCreateEvent>>()

//    @Suppress("UNCHECKED_CAST")
//    fun <T : Command<T>> getOrNull(key: AttributeKey<T>): T? = commands[key]?.let { it as T }
//
//    fun getOrNull(key: String) = commands.filter { it.key.name == key }.values.firstOrNull()

    class Config {
        internal val dispatcher = CommandDispatcher<MessageCreateEvent>()
        internal lateinit var commandPrefix: GuildId

        fun commandPrefix(block: GuildId) {
            commandPrefix = block
        }
        
        fun registerCommand(command: ChatCommand) = command.register(dispatcher)

        fun registerCommands(vararg commands: ChatCommand) = commands.forEach { it.register(dispatcher) }
    }

    /**
     * Adds the functionality to the [DiscordClient] to easily listen to and parse user commands. Must be configured
     * with command prefix and commands.
     */
    companion object : Feature<Config, ChatCommands>("commands") {

        override fun install(client: DiscordClient, configuration: Config.() -> Unit): ChatCommands {
            return ChatCommands(Config().apply(configuration)).also { feature ->
                client.listen<MessageCreateEvent>()
                    .filter { event -> // only accept messages from real users
                        event.message.author
                            .map { !it.isBot }
                            .orElse(false)
                    }
                    .map { event -> event to feature.commandPrefixFor(event.guildId) }
                    .filter { (event, commandPrefix) -> // check if message starts with command prefix or bot is mentioned
                        event.message.content
                            .map { it.startsWith(commandPrefix) }
                            .orElse(false)
                            //.orElseGet { event.message.userMentionIds.contains(client.selfId.get()) }
                    }
                    .map { (event, commandPrefix) ->
                        event.message.content.get().drop(commandPrefix.length).let { commandContent ->
                            feature.cache[commandContent]?.let { cachedResult ->
                                feature.dispatcher.execute(cachedResult)
                            }.ifNull {
                                val result = feature.dispatcher.parse(commandContent, event)
                                feature.dispatcher.execute(result)
                                feature.cache[commandContent] = result
                            }
                        }
                    }
                    .onErrorContinue { t, u -> t.printStackTrace() }
                    .subscribe()
            }
        }
    }
}

typealias GuildId = (guildId: Optional<Snowflake>) -> String
