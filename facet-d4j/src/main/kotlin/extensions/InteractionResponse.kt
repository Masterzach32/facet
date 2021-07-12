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

import discord4j.core.spec.*
import discord4j.discordjson.json.*
import discord4j.rest.interaction.*
import io.facet.discord.dsl.*

/**
 * Create and send a new followup message using the provided spec.
 * This uses a webhook tied to the interaction ID and token.
 */
public suspend fun InteractionResponse.sendFollowupMessage(spec: WebhookExecuteSpec): MessageData =
    createFollowupMessage(spec.asRequest()).await()

/**
 * Create and send a new followup message, using the buildBlock to build the request.
 * This uses a webhook tied to the interaction ID and token.
 */
public suspend fun InteractionResponse.sendFollowupMessage(buildBlock: WebhookMessageBuilder.() -> Unit): MessageData =
    sendFollowupMessage(webhookMessage(buildBlock))
