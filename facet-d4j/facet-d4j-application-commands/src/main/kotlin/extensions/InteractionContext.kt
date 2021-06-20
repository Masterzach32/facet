package io.facet.discord.appcommands.extensions

import discord4j.discordjson.json.*
import discord4j.rest.util.*
import io.facet.discord.appcommands.*
import io.facet.discord.dsl.*
import io.facet.discord.extensions.*

/*
 * facet - Created on 6/6/2021
 * Author: Zach Kozar
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at the project root.
 */

/**
 * @author Zach Kozar
 * @version 6/6/2021
 */

suspend fun InteractionContext.acknowledge() = event.acknowledge().await()

suspend fun InteractionContext.acknowledgeEphemeral() = event.acknowledgeEphemeral().await()

suspend fun InteractionContext.createFollowupMessage(content: String) = event
    .interactionResponse
    .createFollowupMessage(content)
    .await()

suspend fun InteractionContext.createFollowupMessage(template: EmbedTemplate): MessageData {
    val request: MultipartRequest<WebhookExecuteRequest> = MultipartRequest.ofRequest(
        WebhookExecuteRequest.builder()
            .addEmbed(template.asRequest())
            .build()
    )

    return event.interactionResponse.createFollowupMessage(request).await()
}