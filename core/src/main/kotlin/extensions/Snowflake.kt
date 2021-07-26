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

package io.facet.discord.extensions

import discord4j.common.util.*
import discord4j.discordjson.*

/**
 * Extension function to turn a [Long] into a [Snowflake]
 */
public fun Long.toSnowflake(): Snowflake = Snowflake.of(this)

/**
 * Extension function to turn [String] into a [Snowflake]
 */
public fun String.toSnowflake(): Snowflake = Snowflake.of(this)

public fun Id.toSnowflake(): Snowflake = asLong().toSnowflake()

public fun Snowflake.asULong(): ULong = asLong().toULong()
