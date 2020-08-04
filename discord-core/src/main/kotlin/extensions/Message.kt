package io.facet.discord.extensions

import discord4j.core.`object`.entity.*
import discord4j.core.`object`.reaction.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*
import reactor.core.publisher.*

/**
 * Gets ALL user mentions, including users specifically mentioned as well as users mentioned in roles.
 */
@Deprecated(message = "Use coroutine-based functions", replaceWith = ReplaceWith("getAllUserMentions()"))
val Message.allUserMentions: Flux<User>
    get() = when {
        mentionsEveryone() -> guild.toFlux().flatMap(Guild::getMembers)
        else -> userMentions.mergeWith(roleMentions.flatMap(Role::members).distinct())
    }

val Message.ourReactions: List<Reaction>
    get() = reactions.filter(Reaction::selfReacted)

/**
 * Gets ALL user mentions, including users specifically mentioned as well as users mentioned in roles.
 */
suspend fun Message.getAllUserMentions(): List<User> = allUserMentions.await()
