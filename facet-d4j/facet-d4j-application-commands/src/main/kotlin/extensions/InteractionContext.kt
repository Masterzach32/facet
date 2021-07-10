package io.facet.discord.appcommands.extensions

import discord4j.core.spec.*
import discord4j.discordjson.json.*
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

suspend fun SlashCommandContext.acknowledge() = event.acknowledge().await()

suspend fun SlashCommandContext.acknowledgeEphemeral() = event.acknowledgeEphemeral().await()

@Deprecated("")
suspend fun SlashCommandContext.createFollowupMessage(content: String) = event
    .interactionResponse
    .createFollowupMessage(content)
    .await()

@Deprecated("")
suspend fun SlashCommandContext.createFollowupMessage(template: EmbedCreateSpec): MessageData {
    return event.interactionResponse.createFollowupMessage(template.asMultipartRequest()).await()
}

@Deprecated("")
suspend fun SlashCommandContext.createFollowupMessage(buildBlock: EmbedBuilder.() -> Unit): MessageData {
    return event.interactionResponse.createFollowupMessage(embed(buildBlock).asMultipartRequest()).await()
}
