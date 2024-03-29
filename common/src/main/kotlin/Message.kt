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

import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.reaction.Reaction
import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import discord4j.core.event.domain.interaction.ComponentInteractionEvent
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent
import discord4j.core.event.domain.message.ReactionAddEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import io.facet.common.dsl.EmbedBuilder
import io.facet.common.dsl.MessageBuilder
import io.facet.common.dsl.and
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow

/**
 * Gets ALL distinct [User] mentions, including users specifically mentioned as well as users mentioned in roles.
 */
@ExperimentalCoroutinesApi
@FlowPreview
public val Message.allUserMentions: Flow<User>
    get() = when {
        mentionsEveryone() -> guild.asFlow().flatMapConcat { it.members.asFlow() }
        else -> userMentions.asFlow()
            .mergeWith(roleMentions.asFlow().flatMapConcat { it.members })
            .distinctBy { it.id }
    }

/**
 * Returns a flow that emits all [members][Member] mentioned on this message.
 */
@ExperimentalCoroutinesApi
@FlowPreview
public val Message.allMemberMentions: Flow<Member>
    get() = allUserMentions.mapNotNull { it as? Member ?: it.asMember(guildId.get()).awaitNullable() }

/**
 * Returns a set of all reactions that our user as added to this message.
 */
public val Message.ourReactions: Set<Reaction>
    get() = reactions.filter(Reaction::selfReacted).toSet()

/**
 * Returns a flow that emits all reaction events on this message.
 */
public val Message.reactionAddEvents: Flow<ReactionAddEvent>
    get() = client.flowOf<ReactionAddEvent>()
        .filter { it.messageId == id }

/**
 * Returns a flow that emits all component interactions on this message.
 */
public val Message.componentEvents: Flow<ComponentInteractionEvent>
    get() = client.flowOf<ComponentInteractionEvent>()
        .filter { it.interaction.message.unwrap()?.id == id }

/**
 * Returns a flow that emits all button interactions on this message.
 */
public val Message.buttonEvents: Flow<ButtonInteractionEvent>
    get() = componentEvents.filterIsInstance()

/**
 * Returns a flow that emits all select menu interactions on this message.
 */
public val Message.selectMenuEvents: Flow<SelectMenuInteractionEvent>
    get() = componentEvents.filterIsInstance()

/**
 * Gets ALL distinct [User] mentions, including users specifically mentioned as well as users mentioned in roles.
 */
@ExperimentalCoroutinesApi
@FlowPreview
public suspend fun Message.getAllUserMentions(): Set<User> = allUserMentions.toSet()

/**
 * Gets ALL distinct [Member] mentions, including users specifically mentioned as well as users mentioned in roles.
 */
@ExperimentalCoroutinesApi
@FlowPreview
public suspend fun Message.getAllMemberMentions(): Set<Member> = allMemberMentions.toSet()

/**
 * Sends a [Message] as a reply to the receiver, building the message from the specified [MessageBuilder].
 */
public suspend fun Message.reply(
    builder: MessageBuilder.() -> Unit
): Message = channel.await().sendMessage {
    builder()
    messageReference = this@reply.id
}

/**
 * Sends a [Message] as a reply to the receiver, building the message from the specified [MessageCreateSpec].
 */
public suspend fun Message.reply(
    spec: MessageCreateSpec
): Message = channel.await().sendMessage(spec.and {
    messageReference = this@reply.id
})

/**
 * Sends a [Message] with an embed as a reply to the receiver, building the embed from the specified [EmbedCreateSpec].
 */
public suspend fun Message.reply(
    spec: EmbedCreateSpec
): Message = reply {
    embed(spec)
}

/**
 * Sends a [Message] as a reply to the receiver, building the message from the specified content.
 */
public suspend fun Message.reply(
    content: String,
    mention: Boolean = true
): Message = reply {
    this.content = content
    allowedMentions {
        repliedUser = mention
    }
}

/**
 * Sends a [Message] with an embed as a reply to the receiver, building the embed from the specified [EmbedBuilder].
 */
public suspend fun Message.replyEmbed(
    mention: Boolean = true,
    builder: EmbedBuilder.() -> Unit
): Message = reply {
    embed(builder)
    allowedMentions {
        repliedUser = mention
    }
}
