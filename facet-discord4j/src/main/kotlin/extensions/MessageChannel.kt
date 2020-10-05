package io.facet.discord.extensions

import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import discord4j.core.spec.*
import reactor.core.publisher.*

/**
 *
 */
fun MessageChannel.createEmbedReceiver(block: EmbedCreateSpec.() -> Unit): Mono<Message> = createEmbed { spec ->
    spec.apply(block)
}
