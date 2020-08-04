package io.facet.discord.commands

import com.mojang.brigadier.*
import discord4j.core.*
import discord4j.core.event.domain.message.*
import io.facet.discord.commands.events.*
import io.facet.discord.commands.extensions.*
import io.facet.discord.event.*
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.*

class ChatCommandListener(private val feature: ChatCommands) : Listener<MessageCreateEvent> by Listener() {

    private val cache = ConcurrentHashMap<String, ParseResults<ChatCommandSource>>()

    override suspend fun on(event: MessageCreateEvent) {
        // if user is bot, dont continue
        if (event.message.author.map { it.isBot }.orElse(true))
            return

        val prefix = feature.commandPrefixFor(event.guildId)

        // make sure message starts with the command prefix for this guild
        if (event.message.content.startsWith(prefix).not())
            return

        val content = event.message.content.drop(prefix.length).trim()
        val parseResults: ParseResults<ChatCommandSource> = cache[content] ?: feature.dispatcher.parse(
            content,
            ChatCommandSource(event, content, prefix)
        )//.also { cache[content] = it } TODO: Fix caching

        if (parseResults.exceptions.isNotEmpty())
            return // TODO user feedback / help

        val aliasUsed = parseResults.reader.string.split(" ").first()
        val commandUsed = feature.commandMap[aliasUsed] ?: error("Could not find registered command: \"${aliasUsed}\"")
        val isGuild = event.guildId.isPresent

        when (commandUsed.scope) {
            Scope.ALL -> feature.dispatcher.executeSuspend(parseResults)
            Scope.GUILD -> if (isGuild) feature.dispatcher.executeSuspend(parseResults)
            Scope.PRIVATE -> if (!isGuild) feature.dispatcher.executeSuspend(parseResults)
        }

        event.client.eventDispatcher.publish(
            CommandExecutedEvent(event.client, event.shardInfo, commandUsed, parseResults.context.source)
        )
    }
}
