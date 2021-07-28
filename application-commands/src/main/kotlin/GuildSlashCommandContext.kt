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

package io.facet.commands

import discord4j.common.util.*
import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import discord4j.core.event.domain.interaction.*
import io.facet.common.*
import kotlinx.coroutines.*

/**
 * The context for an interaction with an [ApplicationCommand] that occurred in a [server][Guild].
 */
public class GuildSlashCommandContext(
    event: SlashCommandEvent,
    scope: CoroutineScope
) : SlashCommandContext(event, scope) {

    /**
     * The ID of the [server][Guild] that this command was used in.
     */
    public val guildId: Snowflake = interaction.guildId.get()

    /**
     * The [user][User] as a [Member] that invoked this command.
     */
    public val member: Member = interaction.member.get()

    /**
     * The server that this command was used in.
     */
    public suspend fun getGuild(): Guild = interaction.guild.await()

    /**
     * The channel that this command was used in.
     */
    public suspend fun getChannel(): GuildMessageChannel = interaction.channel.await() as GuildMessageChannel
}
