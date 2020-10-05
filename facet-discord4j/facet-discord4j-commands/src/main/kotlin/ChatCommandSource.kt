package io.facet.discord.commands

import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import discord4j.core.event.domain.message.*
import io.facet.core.extensions.*
import io.facet.discord.extensions.*
import kotlinx.coroutines.*

/**
 * Command source for chat commands. Provides easy access to the message event objects.
 */
class ChatCommandSource(
    val event: MessageCreateEvent,
    val command: String,
    val prefixUsed: String,
    scope: CoroutineScope
) : CoroutineScope by scope {

    val client: GatewayDiscordClient = event.client
    val guildId: Snowflake? = event.guildId.value
    val message: Message = event.message
    val user: User = message.author.get()

    val member: Member = event.member.get()

    suspend fun getGuild(): Guild = event.guild.await()
    suspend fun getChannel(): MessageChannel = event.message.channel.await()
    suspend fun getGuildChannel(): GuildMessageChannel = getChannel() as GuildMessageChannel

    override fun equals(other: Any?) = other is ChatCommandSource &&
            command == other.command &&
            message.id == other.message.id

    override fun hashCode(): Int {
        var result = command.hashCode()
        result = 31 * result + message.id.hashCode()
        return result
    }
}