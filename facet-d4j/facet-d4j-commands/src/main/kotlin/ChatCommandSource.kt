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
public class ChatCommandSource(
    public val event: MessageCreateEvent,
    public val command: String,
    public val prefixUsed: String,
    scope: CoroutineScope
) : CoroutineScope by scope {

    public val client: GatewayDiscordClient = event.client
    public val guildId: Snowflake? = event.guildId.unwrap()
    public val message: Message = event.message
    public val user: User = message.author.get()

    public val member: Member by lazy { event.member.get() }

    public suspend fun getGuild(): Guild = event.guild.await()
    public suspend fun getChannel(): MessageChannel = event.message.channel.await()
    public suspend fun getGuildChannel(): GuildMessageChannel = getChannel() as GuildMessageChannel

    override fun equals(other: Any?): Boolean = other is ChatCommandSource &&
            command == other.command &&
            message.id == other.message.id

    override fun hashCode(): Int {
        var result = command.hashCode()
        result = 31 * result + message.id.hashCode()
        return result
    }
}