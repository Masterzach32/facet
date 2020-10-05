package io.facet.discord.extensions

import discord4j.core.`object`.entity.*
import discord4j.core.`object`.reaction.*
import io.facet.core.extensions.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*

/**
 * Gets ALL distinct user mentions, including users specifically mentioned as well as users mentioned in roles.
 */
val Message.allUserMentions: Flow<User>
    get() = when {
        mentionsEveryone() -> guild.asFlow().flatMapConcat { it.members.asFlow() }
        else -> userMentions.asFlow()
            .mergeWith(roleMentions.asFlow().flatMapMerge { it.members })
            .distinctUntilChanged()
    }

val Message.ourReactions: List<Reaction>
    get() = reactions.filter(Reaction::selfReacted)

/**
 * Gets ALL distinct user mentions, including users specifically mentioned as well as users mentioned in roles.
 */
suspend fun Message.getAllUserMentions(): Set<User> = allUserMentions.toSet()
