package io.facet.discord.extensions

import discord4j.core.*
import discord4j.core.shard.*
import discord4j.gateway.*
import reactor.core.publisher.*

/**
 * Configures the gateway client.
 */
inline fun GatewayBootstrap<GatewayOptions>.withFeatures(
    crossinline configureBlock: GatewayDiscordClient.() -> Unit
): Mono<Void> = withGateway { gateway ->
    configureBlock.invoke(gateway)

    Mono.empty<Unit>()
}
