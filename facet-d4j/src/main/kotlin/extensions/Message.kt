package io.facet.discord.extensions

import discord4j.core.`object`.entity.*
import discord4j.core.`object`.reaction.*
import discord4j.core.event.domain.interaction.*
import discord4j.core.event.domain.message.*
import discord4j.core.spec.*
import io.facet.core.extensions.*
import io.facet.discord.dsl.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*

/**
 * Gets ALL distinct user mentions, including users specifically mentioned as well as users mentioned in roles.
 */
@ExperimentalCoroutinesApi
@FlowPreview
val Message.allUserMentions: Flow<User>
    get() = when {
        mentionsEveryone() -> guild.asFlow().flatMapConcat { it.members.asFlow() }
        else -> userMentions.asFlow()
            .mergeWith(roleMentions.asFlow().flatMapConcat { it.members })
            .distinctUntilChanged()
    }

@ExperimentalCoroutinesApi
@FlowPreview
val Message.allMemberMentions: Flow<Member>
    get() = allUserMentions.mapNotNull { it as? Member ?: it.asMember(guildId.get()).awaitNullable() }

val Message.ourReactions: Set<Reaction>
    get() = reactions.filter(Reaction::selfReacted).toSet()


val Message.reactionAddEvents: Flow<ReactionAddEvent>
    get() = client.flowOf<ReactionAddEvent>()
        .filter { it.messageId == id }

val Message.buttonEvents: Flow<ButtonInteractEvent>
    get() = client.flowOf<ButtonInteractEvent>()
        .filter { it.interaction.message.unwrap()?.id == id }

/**
 * Gets ALL distinct user mentions, including users specifically mentioned as well as users mentioned in roles.
 */
@ExperimentalCoroutinesApi
@FlowPreview
suspend fun Message.getAllUserMentions(): Set<User> = allUserMentions.toSet()

@ExperimentalCoroutinesApi
@FlowPreview
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
