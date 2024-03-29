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

import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.event.domain.interaction.SlashCommandEvent
import io.facet.common.await
import io.facet.common.awaitNullable
import io.facet.common.unwrap
import kotlinx.coroutines.CoroutineScope

/**
 * The context for an interaction with an [ApplicationCommand] that could have been used in a DM.
 */
public class GlobalSlashCommandContext(
    event: SlashCommandEvent,
    scope: CoroutineScope
) : SlashCommandContext(event, scope) {

    /**
     * The ID of the [server][Guild] that this command was used in. Is null if the command was used in a DM.
     */
    public val guildId: Snowflake? = interaction.guildId.unwrap()

    /**
     * The [user][User] as a [Member] that invoked this command. Is null if the command was used in a DM.
     */
    public val member: Member? = interaction.member.unwrap()

    /**
     * The server that this command was used in. Is null if the command was used in a DM.
     */
    public suspend fun getGuild(): Guild? = interaction.guild.awaitNullable()

    /**
     * The channel that this command was used in.
     */
    public suspend fun getChannel(): MessageChannel = interaction.channel.await()
}
