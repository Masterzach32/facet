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

package io.facet.discord.extensions

import discord4j.core.*
import discord4j.core.`object`.entity.*
import discord4j.core.event.domain.interaction.*
import discord4j.core.spec.*
import discord4j.rest.interaction.*
import io.facet.discord.dsl.*

/**
 * A handler for common operations related to an interaction followup response.
 */
public interface GatewayInteractionResponse : InteractionResponse {

    /**
     * The gateway client associated with this interaction.
     */
    public val client: GatewayDiscordClient

    /**
     * Create and send a new followup message with the provided content.
     * This uses a webhook tied to the interaction ID and token.
     */
    public suspend fun sendFollowupMessage(content: String): Message

    /**
     * Create and send a new followup message using the provided [WebhookExecuteSpec].
     * This uses a webhook tied to the interaction ID and token.
     */
    public suspend fun sendFollowupMessage(spec: WebhookExecuteSpec): Message
}

private class EventInteractionResponse(
    event: InteractionCreateEvent
) : GatewayInteractionResponse, InteractionResponse by event.interactionResponse {

    override val client: GatewayDiscordClient = event.client

    override suspend fun sendFollowupMessage(content: String): Message =
        Message(client, createFollowupMessage(content).await())

    override suspend fun sendFollowupMessage(spec: WebhookExecuteSpec): Message =
        Message(client, createFollowupMessage(spec.asRequest()).await())
}

/**
 * The handler for common operations related to an interaction followup response associated with this event.
 */
public val InteractionCreateEvent.gatewayInteractionResponse: GatewayInteractionResponse
    get() = EventInteractionResponse(this)

/**
 * Create and send a new followup message, using the [WebhookMessageBuilder] to build the request.
 * This uses a webhook tied to the interaction ID and token.
 */
public suspend fun GatewayInteractionResponse.sendFollowupMessage(build: WebhookMessageBuilder.() -> Unit): Message =
    sendFollowupMessage(webhookMessage(build))

/**
 * Create and send a new followup message containing an embed from the specified [EmbedCreateSpec].
 * This uses a webhook tied to the interaction ID and token.
 */
public suspend fun GatewayInteractionResponse.sendFollowupMessage(spec: EmbedCreateSpec): Message =
    sendFollowupMessage { embed(spec) }
