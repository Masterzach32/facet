package io.facet.discord.event

import discord4j.core.*
import discord4j.core.event.domain.*
import io.facet.discord.extensions.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*
import org.slf4j.*
import kotlin.coroutines.*

/**
 * Creates and launches a new coroutine, which listens to the specified [Event] type and calls the
 * block function whenever a new event of that type is received by the gateway.
 */
inline fun <reified E : Event> CoroutineScope.listener(
    gateway: GatewayDiscordClient,
    context: CoroutineContext = EmptyCoroutineContext,
    capacity: Int = Channel.RENDEZVOUS,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend CoroutineScope.(E) -> Unit
) = launch(context, start) {
    val logger = LoggerFactory.getLogger("EventListener<${E::class.simpleName}>")
    gateway.on<E>().asFlow().filterNotNull().buffer(capacity).collect { event ->
        try {
            block(event)
        } catch (e: CancellationException) {
            throw CancellationException("Child coroutine of EventListener<${E::class.simpleName}> was cancelled.", e)
        } catch (e: Throwable) {
            logger.warn("Exception caught while processing event: ${E::class.simpleName}", e)
        }
    }
}

/**
 * Creates and launches a new coroutine, which launches an actor coroutine and forwards gateway events of the
 * specified type to it's [ReceiveChannel].
 */
@ObsoleteCoroutinesApi
inline fun <reified E : Event> CoroutineScope.actorListener(
    gateway: GatewayDiscordClient,
    context: CoroutineContext = EmptyCoroutineContext,
    capacity: Int = Channel.RENDEZVOUS,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    noinline onCompletion: CompletionHandler? = null,
    noinline block: suspend ActorScope<E>.() -> Unit
) = launch(context, start) {
    val eventChannel = actor(capacity = capacity, onCompletion = onCompletion, block = block)
    gateway.on<E>().asFlow().filterNotNull().collect { event ->
        eventChannel.send(event)
    }
}
