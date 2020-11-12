package io.facet.discord.extensions

import discord4j.core.*
import discord4j.core.shard.*
import discord4j.gateway.*
import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.*
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

/**
 * Configures the gateway client.
 */
inline fun GatewayBootstrap<GatewayOptions>.withFeatures(
    crossinline configureBlock: CoroutineScope.(GatewayDiscordClient) -> Unit
): Mono<Void> = withGateway { gateway ->
    mono {
        configureBlock(gateway)
    }
}
