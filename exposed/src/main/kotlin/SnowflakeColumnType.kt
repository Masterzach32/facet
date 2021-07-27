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

package io.facet.exposed

import discord4j.common.util.*
import io.facet.common.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.vendors.*

/**
 * Numeric column for storing 64-bit [Snowflake] IDs
 */
public class SnowflakeColumnType : ColumnType() {

    override fun sqlType(): String = currentDialect.dataTypeProvider.longType()

    override fun valueFromDB(value: Any): Any = when (value) {
        is Long -> value
        is Number -> value.toLong()
        is String -> value.toLong()
        else -> error("Expected value of type Long, instead found $value of type ${value::class.qualifiedName}")
    }.toSnowflake()

    override fun notNullValueToDB(value: Any): Any = (value as Snowflake).asLong()

    override fun nonNullValueToString(value: Any): String = notNullValueToDB(value).toString()

    internal companion object {
        internal val INSTANCE = SnowflakeColumnType()
    }
}

/**
 * Creates a numeric column, with the specified [name], for storing unsigned 64-bit snowflake IDs.
 */
public fun Table.snowflake(name: String): Column<Snowflake> = registerColumn(name, SnowflakeColumnType())
