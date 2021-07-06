package io.facet.discord.appcommands.extensions

import discord4j.discordjson.json.*
import discord4j.rest.util.*
import io.facet.discord.dsl.*
import io.facet.discord.extensions.*

fun EmbedTemplate.asMultipartRequest(): MultipartRequest<WebhookExecuteRequest> = MultipartRequest.ofRequest(
    WebhookExecuteRequest.builder()
        .addEmbed(asRequest())
        .build()
)