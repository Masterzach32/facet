/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.facet.common

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.EventDispatcher
import discord4j.core.event.domain.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ActorScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.reactive.asFlow
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Helper function to make listening to discord events shorter.
 */
public inline fun <reified E : Event> EventDispatcher.on(): Flux<E> = on(E::class.java)

/**
 * Returns a [Flow] of the specified event type.
 */
public inline fun <reified E : Event> EventDispatcher.flowOf(): Flow<E> = on<E>().asFlow()

/**
 * Helper function to make listening to discord events shorter.
 */
public inline fun <reified E : Event> GatewayDiscordClient.on(): Flux<E> = on(E::class.java)

/**
 * Returns a [Flow] of the specified event type.
 */
public inline fun <reified E : Event> GatewayDiscordClient.flowOf(): Flow<E> = on<E>().asFlow()

/**
 * Creates and launches a new coroutine, which listens to the specified [Event] type and calls the
 * block function whenever a new event of that type is received by the gateway.
 */
public inline fun <reified E : Event> EventDispatcher.listener(
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    capacity: Int = Channel.RENDEZVOUS,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend CoroutineScope.(E) -> Unit
): Job = scope.launch(context, start) {
    val logger = LoggerFactory.getLogger("EventListener<${E::class.simpleName}>")
    this@listener.flowOf<E>().filterNotNull().buffer(capacity).collect { event ->
        try {
            block(event)
        } catch (e: CancellationException) {
            throw CancellationException("Parent coroutine of event listener was cancelled.", e)
        } catch (e: Throwable) {
            logger.warn("Exception caught while processing event: ${E::class.simpleName}", e)
        }
    }
}

/**
 * Creates and launches a new coroutine, which listens to the specified [Event] type and calls the
 * block function whenever a new event of that type is received by the gateway.
 */
public inline fun <reified E : Event> GatewayDiscordClient.listener(
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    capacity: Int = Channel.RENDEZVOUS,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend CoroutineScope.(E) -> Unit
): Job = eventDispatcher.listener(scope, context, capacity, start, block)

/**
 * Creates and launches a new coroutine, which launches an actor coroutine and forwards gateway events of the
 * specified type to it's [ReceiveChannel].
 */
@ObsoleteCoroutinesApi
public inline fun <reified E : Event> EventDispatcher.actorListener(
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    capacity: Int = Channel.RENDEZVOUS,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    noinline onCompletion: CompletionHandler? = null,
    noinline block: suspend ActorScope<E>.() -> Unit
): Job = scope.launch(context, start) {
    val eventChannel = actor(capacity = capacity, onCompletion = onCompletion, block = block)
    this@actorListener.flowOf<E>().filterNotNull().collect { event ->
        eventChannel.send(event)
    }
}

/**
 * Creates and launches a new coroutine, which launches an actor coroutine and forwards gateway events of the
 * specified type to it's [ReceiveChannel].
 */
@ObsoleteCoroutinesApi
public inline fun <reified E : Event> GatewayDiscordClient.actorListener(
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    capacity: Int = Channel.RENDEZVOUS,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    noinline onCompletion: CompletionHandler? = null,
    noinline block: suspend ActorScope<E>.() -> Unit
): Job = eventDispatcher.actorListener(scope, context, capacity, start, onCompletion, block)

