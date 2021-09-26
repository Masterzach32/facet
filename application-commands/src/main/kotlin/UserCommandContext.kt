package io.facet.commands

import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.User
import discord4j.core.event.domain.interaction.UserInteractionEvent
import discord4j.core.retriever.EntityRetrievalStrategy
import io.facet.common.await
import kotlinx.coroutines.CoroutineScope

public sealed class UserCommandContext(
    event: UserInteractionEvent,
    scope: CoroutineScope
) : ApplicationCommandContext<UserInteractionEvent>(event, scope) {

    public val targetId: Snowflake = event.targetId

    public open suspend fun getTargetUser(): User = event.targetUser.await()

    public open suspend fun getTargetUser(retrievalStrategy: EntityRetrievalStrategy): User =
        event.getTargetUser(retrievalStrategy).await()
}

/**
 * The context for an interaction with an [ApplicationCommand] that could have been used in a DM.
 */
public class GlobalUserCommandContext(
    event: UserInteractionEvent,
    scope: CoroutineScope
) : UserCommandContext(event, scope),
    GlobalCommandContext by GlobalCommandContextImpl(event.interaction)

/**
 * The context for an interaction with an [ApplicationCommand] that occurred in a [server][Guild].
 */
public class GuildUserCommandContext(
    event: UserInteractionEvent,
    scope: CoroutineScope
) : UserCommandContext(event, scope),
    GuildCommandContext by GuildCommandContextImpl(event.interaction) {

    override suspend fun getTargetUser(): Member =
        super.getTargetUser().asMember(guildId).await()

    override suspend fun getTargetUser(retrievalStrategy: EntityRetrievalStrategy): Member =
        super.getTargetUser(retrievalStrategy).asMember(guildId, retrievalStrategy).await()
}
