package io.facet.commands

import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Message
import discord4j.core.event.domain.interaction.MessageInteractionEvent
import discord4j.core.retriever.EntityRetrievalStrategy
import io.facet.common.await
import kotlinx.coroutines.CoroutineScope

public sealed class MessageCommandContext(
    event: MessageInteractionEvent,
    scope: CoroutineScope
) : ApplicationCommandContext<MessageInteractionEvent>(event, scope) {

    public val targetId: Snowflake = event.targetId

    public suspend fun getTargetMessage(): Message = event.targetMessage.await()

    public suspend fun getTargetMessage(retrievalStrategy: EntityRetrievalStrategy): Message =
        event.getTargetMessage(retrievalStrategy).await()
}

/**
 * The context for an interaction with an [ApplicationCommand] that could have been used in a DM.
 */
public class GlobalMessageCommandContext(
    event: MessageInteractionEvent,
    scope: CoroutineScope
) : MessageCommandContext(event, scope),
    GlobalCommandContext by GlobalCommandContextImpl(event.interaction)

/**
 * The context for an interaction with an [ApplicationCommand] that occurred in a [server][Guild].
 */
public class GuildMessageCommandContext(
    event: MessageInteractionEvent,
    scope: CoroutineScope
) : MessageCommandContext(event, scope),
    GuildCommandContext by GuildCommandContextImpl(event.interaction)
