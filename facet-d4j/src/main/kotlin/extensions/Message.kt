package io.facet.discord.extensions

import discord4j.core.`object`.entity.*
import discord4j.core.`object`.reaction.*
import discord4j.core.event.domain.message.*
import discord4j.core.spec.*
import io.facet.core.extensions.*
import io.facet.discord.dsl.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*

/**
 * Gets ALL distinct user mentions, including users specifically mentioned as well as users mentioned in roles.
 */
val Message.allUserMentions: Flow<User>
    get() = when {
        mentionsEveryone() -> guild.asFlow().flatMapConcat { it.members.asFlow() }
        else -> userMentions.asFlow()
            .mergeWith(roleMentions.asFlow().flatMapConcat { it.members })
            .distinctUntilChanged()
    }

val Message.allMemberMentions: Flow<Member>
    get() = allUserMentions.mapNotNull { it as? Member ?: it.asMember(guildId.get()).awaitNullable() }

val Message.ourReactions: Set<Reaction>
    get() = reactions.filter(Reaction::selfReacted).toSet()

/**
 * Gets ALL distinct user mentions, including users specifically mentioned as well as users mentioned in roles.
 */
suspend fun Message.getAllUserMentions(): Set<User> = allUserMentions.toSet()

suspend fun Message.getAllMemberMentions(): Set<Member> = allMemberMentions.toSet()

/**
 * Sends a [Message] as a reply to the receiver, building the message from the specified [MessageBuilder].
 */
suspend fun Message.reply(
    builder: MessageBuilder.() -> Unit
) = channel.await().sendMessage(message {
    builder()
    messageReference = this@reply.id
})

/**
 * Sends a [Message] as a reply to the receiver, building the message from the specified [MessageCreateSpec].
 */
suspend fun Message.reply(
    spec: MessageCreateSpec
) = channel.await().sendMessage(spec.and {
    messageReference = this@reply.id
})

/**
 * Sends a [Message] with an embed as a reply to the receiver, building the embed from the specified [EmbedCreateSpec].
 */
suspend fun Message.reply(
    spec: EmbedCreateSpec
) = reply {
    embed(spec)
}

/**
 * Sends a [Message] as a reply to the receiver, building the message from the specified content.
 */
suspend fun Message.reply(
    content: String,
    mention: Boolean = true
) = reply {
    this.content = content
    allowedMentions {
        repliedUser = mention
    }
}

/**
 * Sends a [Message] with an embed as a reply to the receiver, building the embed from the specified [EmbedBuilder].
 */
suspend fun Message.replyEmbed(
    mention: Boolean = true,
    builder: EmbedBuilder.() -> Unit
) = reply {
    embed(builder)
    allowedMentions {
        repliedUser = mention
    }
}

val Message.reactionAddEvents: Flow<ReactionAddEvent>
    get() = client.flowOf<ReactionAddEvent>()
        .filter { it.messageId == id }
