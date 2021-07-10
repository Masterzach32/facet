package io.facet.discord.extensions

import discord4j.core.spec.*
import discord4j.discordjson.json.*
import discord4j.rest.interaction.*
import io.facet.discord.dsl.*

suspend fun InteractionResponse.createFollowupMessage(spec: WebhookExecuteSpec): MessageData =
    createFollowupMessage(spec.asRequest()).await()

suspend fun InteractionResponse.createFollowupMessage(buildBlock: WebhookMessageBuilder.() -> Unit): MessageData =
    createFollowupMessage(webhookMessage(buildBlock))
