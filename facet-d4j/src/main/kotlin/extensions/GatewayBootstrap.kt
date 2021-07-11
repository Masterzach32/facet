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
@Deprecated("Use withPlugins()", ReplaceWith("withPlugins(configureBlock)"))
public fun GatewayBootstrap<GatewayOptions>.withFeatures(
    configureBlock: suspend EventDispatcher.(CoroutineScope) -> Unit
): GatewayBootstrap<GatewayOptions> = withPlugins(configureBlock)

/**
 * Configures the event dispatcher before any events can be received.
 */
public fun GatewayBootstrap<GatewayOptions>.withPlugins(
    configureBlock: suspend EventDispatcher.(CoroutineScope) -> Unit
): GatewayBootstrap<GatewayOptions> = withEventDispatcher { dispatcher ->
    mono {
        configureBlock(dispatcher, this)
    }
}

/**
 * Configures the gateway client.
 */
@Deprecated("")
public fun GatewayBootstrap<GatewayOptions>.withFeatures(
    configureBlock: suspend GatewayDiscordClient.() -> Unit
): Mono<Void> = withGateway { gateway ->
    mono {
        configureBlock(gateway)
    }
}

/**
 * Configures the gateway client.
 */
public fun GatewayBootstrap<GatewayOptions>.withPlugins(
    configureBlock: suspend GatewayDiscordClient.(CoroutineScope) -> Unit
): Mono<Void> = withGateway { gateway ->
    mono {
        configureBlock(gateway, this)
    }
}
