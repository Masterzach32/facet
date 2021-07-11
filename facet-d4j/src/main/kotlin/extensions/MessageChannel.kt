package io.facet.discord.extensions

import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import discord4j.core.spec.*
import io.facet.discord.dsl.*

/**
 * Builds a message spec with only content and sends it in the specified channel.
 */
suspend fun MessageChannel.sendMessage(content: String): Message = createMessage(content).await()

/**
 * Builds a [MessageCreateSpec] and sends it in the specified channel.
 */
suspend fun MessageChannel.sendMessage(
    block: MessageBuilder.() -> Unit
): Message = createMessage(message(block)).await()

/**
 * Sends a message in the specified channel based on the [MessageCreateSpec].
 */
suspend fun MessageChannel.sendMessage(
    template: MessageCreateSpec
): Message = createMessage(template).await()

/**
 * Builds an [EmbedCreateSpec] and sends it in the specified channel.
 */
suspend fun MessageChannel.sendEmbed(
    block: EmbedBuilder.() -> Unit
): Message = createMessage(embed(block)).await()

/**
 * Sends a message with an embed in the specified channel based on the [EmbedCreateSpec].
 */
suspend fun MessageChannel.sendMessage(
    vararg specs: EmbedCreateSpec
): Message = createMessage(*specs).await()

/**
 * Sends an embed in the specified channel based on the [EmbedCreateSpec].
 */
@Deprecated("Use sendMessage", ReplaceWith("sendMessage()"))
suspend fun MessageChannel.sendEmbed(
    spec: EmbedCreateSpec
): Message = createMessage(spec).await()
