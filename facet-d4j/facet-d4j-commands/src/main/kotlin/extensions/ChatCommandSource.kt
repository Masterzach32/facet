package io.facet.discord.commands.extensions

import discord4j.core.`object`.entity.*
import discord4j.core.spec.*
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
 * Builds a [MessageCreateSpec] and sends it in the channel that the command was invoked in.
 */
suspend fun ChatCommandSource.respondMessage(
    builder: MessageBuilder.() -> Unit
): Message = getChannel().sendMessage(builder)

/**
 * Sends a message based on the [MessageCreateSpec] in the channel that the command was invoked in.
 */
suspend fun ChatCommandSource.respondMessage(
    template: MessageCreateSpec
): Message = getChannel().sendMessage(template)

/**
 * Builds an [EmbedCreateSpec] and sends it in the channel that the command was invoked in.
 */
suspend fun ChatCommandSource.respondEmbed(
    builder: EmbedBuilder.() -> Unit
): Message = getChannel().sendEmbed(builder)

/**
 * Sends an embed based on the [EmbedCreateSpec] in the channel that the command was invoked in.
 */
suspend fun ChatCommandSource.respondMessage(
    vararg specs: EmbedCreateSpec
): Message = getChannel().sendMessage(*specs)

/**
 * Sends an embed based on the [EmbedCreateSpec] in the channel that the command was invoked in.
 */
@Deprecated("Use respondMessage", ReplaceWith("respondMessage(spec)"))
suspend fun ChatCommandSource.respondEmbed(
    spec: EmbedCreateSpec
): Message = respondMessage(spec)
