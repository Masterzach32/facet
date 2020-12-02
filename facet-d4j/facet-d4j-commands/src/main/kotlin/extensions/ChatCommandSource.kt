package io.facet.discord.commands.extensions

import discord4j.core.`object`.entity.*
import io.facet.discord.commands.*
import io.facet.discord.dsl.*
import io.facet.discord.extensions.*

/**
 * Builds a message spec with only content and sends it in the channel the command was invoked in.
 */
suspend fun ChatCommandSource.respondMessage(
    content: String
): Message = getChannel().sendMessage(content)

/**
 * Builds a [MessageTemplate] and sends it in the channel that the command was invoked in.
 */
suspend fun ChatCommandSource.respondMessage(
    builder: MessageBuilder.() -> Unit
): Message = getChannel().sendMessage(builder)

/**
 * Sends a message based on the [MessageTemplate] in the channel that the command was invoked in.
 */
suspend fun ChatCommandSource.respondMessage(
    template: MessageTemplate
): Message = getChannel().sendMessage(template)

/**
 * Builds an [EmbedTemplate] and sends it in the channel that the command was invoked in.
 */
suspend fun ChatCommandSource.respondEmbed(
    builder: EmbedBuilder.() -> Unit
): Message = getChannel().sendEmbed(builder)

/**
 * Sends an embed based on the [EmbedTemplate] in the channel that the command was invoked in.
 */
suspend fun ChatCommandSource.respondEmbed(
    template: EmbedTemplate
): Message = getChannel().sendEmbed(template)
