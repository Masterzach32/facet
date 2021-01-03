package io.facet.discord.extensions

import discord4j.core.*
import discord4j.core.event.*
import discord4j.core.shard.*
import discord4j.gateway.*
import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.*
import reactor.core.publisher.*

/**
 * Configures the event dispatcher before any events can be received.
 */
inline fun GatewayBootstrap<GatewayOptions>.withFeatures(
    crossinline configureBlock: suspend EventDispatcher.(CoroutineScope) -> Unit
): GatewayBootstrap<GatewayOptions> = withEventDispatcher { dispatcher ->
    mono {
        configureBlock(dispatcher, this)
    }
}

/**
 * Configures the gateway client.
 */
@Deprecated("")
inline fun GatewayBootstrap<GatewayOptions>.withFeatures(
    crossinline configureBlock: suspend GatewayDiscordClient.() -> Unit
): Mono<Void> = withGateway { gateway ->
    mono {
        configureBlock(gateway)
    }
}

/**
 * Configures the gateway client.
 */
inline fun GatewayBootstrap<GatewayOptions>.withFeatures(
    crossinline configureBlock: suspend GatewayDiscordClient.(CoroutineScope) -> Unit
): Mono<Void> = withGateway { gateway ->
    mono {
        configureBlock(gateway, this)
    }
}
