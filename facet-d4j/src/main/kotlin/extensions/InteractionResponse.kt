package io.facet.discord.extensions

import discord4j.core.spec.*
import discord4j.discordjson.json.*
import discord4j.rest.interaction.*
import io.facet.discord.dsl.*

/**
 * Create and send a new followup message using the provided spec.
 * This uses a webhook tied to the interaction ID and token.
 */
@Deprecated("Use sendXX", ReplaceWith("sendFollowupMessage(spec)"))
suspend fun InteractionResponse.createFollowupMessage(spec: WebhookExecuteSpec): MessageData =
    sendFollowupMessage(spec)

/**
 * Create and send a new followup message, using the buildBlock to build the request.
 * This uses a webhook tied to the interaction ID and token.
 */
@Deprecated("Use sendXX", ReplaceWith("sendFollowupMessage(buildBlock)"))
suspend fun InteractionResponse.createFollowupMessage(buildBlock: WebhookMessageBuilder.() -> Unit): MessageData =
    sendFollowupMessage(buildBlock)

/**
 * Create and send a new followup message using the provided spec.
 * This uses a webhook tied to the interaction ID and token.
 */
suspend fun InteractionResponse.sendFollowupMessage(spec: WebhookExecuteSpec): MessageData =
    createFollowupMessage(spec.asRequest()).await()

/**
 * Create and send a new followup message, using the buildBlock to build the request.
 * This uses a webhook tied to the interaction ID and token.
 */
suspend fun InteractionResponse.sendFollowupMessage(buildBlock: WebhookMessageBuilder.() -> Unit): MessageData =
    createFollowupMessage(webhookMessage(buildBlock))
