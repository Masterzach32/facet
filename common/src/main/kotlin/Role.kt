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

package io.facet.common

import discord4j.core.`object`.entity.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*

/**
 * Gets the [members][Member] with this role by requesting the members of this guild and filtering by this role's [Snowflake] ID.
 */
@FlowPreview
public val Role.members: Flow<Member>
    get() = guild.asFlow()
        .flatMapConcat { it.members.asFlow() }
        .filter { it.roleIds.contains(id) }

/**
 * Gets the [members][Member] with this role by requesting the members of this guild and filtering by this role's [Snowflake] ID.
 */
@FlowPreview
public suspend fun Role.getMembers(): List<Member> = members.toList()
