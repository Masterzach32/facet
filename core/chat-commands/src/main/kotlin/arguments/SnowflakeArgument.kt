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

package io.facet.discord.commands.arguments

import com.mojang.brigadier.*
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.arguments.LongArgumentType.*
import discord4j.common.util.*
import io.facet.discord.extensions.*

public object SnowflakeArgument : ArgumentType<Snowflake> {

    private val longArgType = longArg(0, Long.MAX_VALUE)

    override fun parse(reader: StringReader): Snowflake = longArgType.parse(reader).toSnowflake()
}
