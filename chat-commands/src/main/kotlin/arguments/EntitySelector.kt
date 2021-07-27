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

package io.facet.chatcommands.arguments

import com.mojang.brigadier.*
import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.entity.*

public abstract class EntitySelector<E : Entity> {

    protected val entities: MutableList<Snowflake> = mutableListOf()

    internal abstract fun parse(reader: StringReader)

    public abstract suspend fun get(client: GatewayDiscordClient, guildId: Snowflake): E

    public abstract suspend fun getMultiple(client: GatewayDiscordClient, guildId: Snowflake): List<E>
}
