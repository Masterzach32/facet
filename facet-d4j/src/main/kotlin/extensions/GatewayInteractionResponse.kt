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
public class GatewayInteractionResponse(
    event: InteractionCreateEvent
) : InteractionResponse by event.interactionResponse {

    public val gateway: GatewayDiscordClient = event.client
}

/**
 * The handler for common operations related to an interaction followup response associated with this event.
 */
public val InteractionCreateEvent.gatewayInteractionResponse: GatewayInteractionResponse
    get() = GatewayInteractionResponse(this)

/**
 * Create and send a new followup message with the provided content.
 * This uses a webhook tied to the interaction ID and token.
 */
public suspend fun GatewayInteractionResponse.sendFollowupMessage(content: String): Message =
    Message(gateway, createFollowupMessage(content).await())

/**
 * Create and send a new followup message using the provided [WebhookExecuteSpec].
 * This uses a webhook tied to the interaction ID and token.
 */
public suspend fun GatewayInteractionResponse.sendFollowupMessage(spec: WebhookExecuteSpec): Message =
    Message(gateway, createFollowupMessage(spec.asRequest()).await())

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
