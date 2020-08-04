package io.facet.discord.extensions

import discord4j.core.`object`.entity.*
import reactor.core.publisher.*

/**
 * Gets the [Member]s with this role by requesting the members of this guild and filtering by this role's [Snowflake] ID.
 */
@Deprecated(message = "Use coroutine-based functions", replaceWith = ReplaceWith("getMembers()"))
val Role.members: Flux<Member>
    get() = guild.toFlux()
        .flatMap(Guild::getMembers)
        .filter { it.roleIds.contains(this.id) }

/**
 * Gets the [Member]s with this role by requesting the members of this guild and filtering by this role's [Snowflake] ID.
 */
suspend fun Role.getMembers(): List<Member> = members.await()
