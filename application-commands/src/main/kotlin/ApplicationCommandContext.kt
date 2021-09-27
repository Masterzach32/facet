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

package io.facet.commands

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.command.Interaction
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import io.facet.common.GatewayInteractionResponse
import io.facet.common.await
import io.facet.common.gatewayInteractionResponse
import kotlinx.coroutines.CoroutineScope

/**
 * The context for an interaction with an application command.
 */
public sealed class ApplicationCommandContext(
    /**
     * The discord interaction event.
     */
    public open val event: ApplicationCommandInteractionEvent,
    scope: CoroutineScope
) : CoroutineScope by scope {

    /**
     * The gateway this event was dispatched from.
     */
    public val client: GatewayDiscordClient
        get() = event.client

    /**
     * The discord interaction for this command context.
     */
    public val interaction: Interaction
        get() = event.interaction

    /**
     * The ID of the [channel][MessageChannel] where this command was used.
     */
    public val channelId: Snowflake
        get() = interaction.channelId

    /**
     * The user that invoked this command.
     */
    public val user: User
        get() = interaction.user

    /**
     * The interaction followup handler.
     */
    public val interactionResponse: GatewayInteractionResponse
        get() = event.gatewayInteractionResponse
}

/**
 * Acknowledges the interaction indicating a response will be edited later. The user sees a loading state,
 * visible to all participants in the invoking channel. For a "only you can see this" response, set [ephemeral] to
 * `true`, or use acknowledgeEphemeral().
 */
@Deprecated("Use deferReply()", ReplaceWith("deferReply(ephemeral)"))
public suspend fun ApplicationCommandContext.acknowledge(ephemeral: Boolean = false): Unit =
    deferReply(ephemeral)

/**
 * Acknowledges the interaction indicating a response will be edited later. The user sees a loading state,
 * visible to all participants in the invoking channel. For a "only you can see this" response, set [ephemeral] to
 * `true`.
 */
public suspend fun ApplicationCommandContext.deferReply(ephemeral: Boolean = false): Unit =
    event.deferReply().withEphemeral(ephemeral).await()

/**
 * Acknowledges the interaction indicating a response will be edited later. Only the invoking user sees a loading state.
 */
@Deprecated("Use deferReply()", ReplaceWith("deferReply(ephemeral = true)"))
public suspend fun ApplicationCommandContext.acknowledgeEphemeral(): Unit = deferReply(ephemeral = true)
