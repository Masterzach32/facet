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

import com.mojang.brigadier.context.*
import discord4j.common.util.*
import io.facet.chatcommands.arguments.*

/**
 * Get the name of the command alias used.
 */
public val CommandContext<ChatCommandSource>.aliasUsed: String
    get() = rootNode.name

public inline fun <reified T> CommandContext<ChatCommandSource>.get(name: String): T = getArgument(name, T::class.java)

public fun CommandContext<ChatCommandSource>.getInt(name: String): Int = get(name)

public fun CommandContext<ChatCommandSource>.getBool(name: String): Boolean = get(name)

public fun CommandContext<ChatCommandSource>.getDouble(name: String): Double = get(name)

public fun CommandContext<ChatCommandSource>.getFloat(name: String): Float = get(name)

public fun CommandContext<ChatCommandSource>.getLong(name: String): Long = get(name)

public fun CommandContext<ChatCommandSource>.getString(name: String): String = get(name)

public fun CommandContext<ChatCommandSource>.getSnowflake(name: String): Snowflake = get(name)

public fun CommandContext<ChatCommandSource>.getMembers(name: String): MemberSelector = get(name)

public fun CommandContext<ChatCommandSource>.getRoles(name: String): RoleSelector = get(name)

public fun CommandContext<ChatCommandSource>.getChannels(name: String): TextChannelSelector = get(name)
