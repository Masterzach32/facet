package io.facet.discord.appcommands.extensions

import discord4j.core.spec.*
import discord4j.discordjson.json.*
import discord4j.rest.util.*

fun EmbedCreateSpec.asMultipartRequest(): MultipartRequest<WebhookExecuteRequest> = MultipartRequest.ofRequest(
    WebhookExecuteRequest.builder()
        .addEmbed(asRequest())
        .build()
)