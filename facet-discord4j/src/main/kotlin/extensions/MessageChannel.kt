package io.facet.discord.extensions

import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import discord4j.core.spec.*
import io.facet.discord.dsl.*
import reactor.core.publisher.*

@Deprecated("Use sendEmbed()", ReplaceWith("sendEmbed()"))
fun MessageChannel.createEmbedReceiver(block: EmbedCreateSpec.() -> Unit): Mono<Message> = createEmbed { spec ->
    spec.apply(block)
}

/**
 * Builds a message spec with only content and sends it in the specified channel.
 */
suspend fun MessageChannel.sendMessage(content: String): Message = createMessage(content).await()

/**
 * Builds a [MessageTemplate] and sends it in the specified channel.
 */
suspend inline fun MessageChannel.sendMessage(
    block: MessageBuilder.() -> Unit
): Message = createMessage(message(block)).await()

/**
 * Sends a message in the specified channel based on the [MessageTemplate].
 */
suspend fun MessageChannel.sendMessage(
    template: MessageTemplate
): Message = createMessage(template).await()

/**
 * Builds an [EmbedTemplate] and sends it in the specified channel.
 */
suspend inline fun MessageChannel.sendEmbed(
    block: EmbedBuilder.() -> Unit
): Message = createEmbed(embed(block)).await()

/**
 * Sends an embed in the specified channel based on the [EmbedTemplate].
 */
suspend fun MessageChannel.sendEmbed(
    template: EmbedTemplate
): Message = createEmbed(template).await()
