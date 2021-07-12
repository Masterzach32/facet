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

package io.facet.discord.commands.events

import discord4j.core.*
import discord4j.core.event.domain.*
import discord4j.gateway.*
import io.facet.discord.commands.*

public abstract class CommandEvent(
    client: GatewayDiscordClient,
    shardInfo: ShardInfo,
    public val command: ChatCommand,
    public val source: ChatCommandSource
) : Event(client, shardInfo)