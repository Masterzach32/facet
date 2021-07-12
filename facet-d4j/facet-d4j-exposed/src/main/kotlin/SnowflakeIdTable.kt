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

package io.facet.discord.exposed

import discord4j.common.util.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*

public open class SnowflakeIdTable(name: String = "", columnName: String = "id") : IdTable<Snowflake>(name) {

    override val id: Column<EntityID<Snowflake>> = snowflake(columnName).entityId()

    override val primaryKey: PrimaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
}
