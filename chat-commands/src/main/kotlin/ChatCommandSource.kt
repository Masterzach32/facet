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

package io.facet.chatcommands

import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import discord4j.core.event.domain.message.*
import discord4j.core.spec.*
import io.facet.common.*
import io.facet.common.dsl.*
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

/**
 * Builds a message spec with only content and sends it in the channel the command was invoked in.
 */
public suspend fun ChatCommandSource.respondMessage(
    content: String
): Message = getChannel().sendMessage(content)

/**
 * Builds a [MessageCreateSpec] and sends it in the channel that the command was invoked in.
 */
public suspend fun ChatCommandSource.respondMessage(
    builder: MessageBuilder.() -> Unit
): Message = getChannel().sendMessage(builder)

/**
 * Sends a message based on the [MessageCreateSpec] in the channel that the command was invoked in.
 */
public suspend fun ChatCommandSource.respondMessage(
    template: MessageCreateSpec
): Message = getChannel().sendMessage(template)

/**
 * Builds an [EmbedCreateSpec] and sends it in the channel that the command was invoked in.
 */
public suspend fun ChatCommandSource.respondEmbed(
    builder: EmbedBuilder.() -> Unit
): Message = getChannel().sendEmbed(builder)

/**
 * Sends an embed based on the [EmbedCreateSpec] in the channel that the command was invoked in.
 */
public suspend fun ChatCommandSource.respondMessage(
    vararg specs: EmbedCreateSpec
): Message = getChannel().sendMessage(*specs)

/**
 * Sends an embed based on the [EmbedCreateSpec] in the channel that the command was invoked in.
 */
@Deprecated("Use respondMessage", ReplaceWith("respondMessage(spec)"))
public suspend fun ChatCommandSource.respondEmbed(
    spec: EmbedCreateSpec
): Message = respondMessage(spec)
