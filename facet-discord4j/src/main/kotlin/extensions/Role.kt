package io.facet.discord.extensions

import discord4j.core.`object`.entity.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*

/**
 * Gets the [Member]s with this role by requesting the members of this guild and filtering by this role's [Snowflake] ID.
 */
val Role.members: Flow<Member>
    get() = guild.asFlow()
        .flatMapConcat { it.members.asFlow() }
        .filter { it.roleIds.contains(id) }

/**
 * Gets the [Member]s with this role by requesting the members of this guild and filtering by this role's [Snowflake] ID.
 */
suspend fun Role.getMembers(): List<Member> = members.toList()
