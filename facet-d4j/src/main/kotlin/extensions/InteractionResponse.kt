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
