package io.facet.discord.event

import discord4j.core.event.domain.*
import kotlin.reflect.*

interface Listener<E : Event> {

    val type: KClass<E>

    suspend fun on(event: E)
}

class ListenerImpl<E : Event>(override val type: KClass<E>) : Listener<E> {

    override suspend fun on(event: E) {
        throw NotImplementedError("Did you forget to override the on(event) method of your ${type.simpleName} listener?")
    }
}

inline fun <reified E : Event> Listener(): Listener<E> = ListenerImpl(E::class)

class ReceiverListenerImpl<E : Event>(
    override val type: KClass<E>,
    private val receiver: suspend E.() -> Unit
): Listener<E> {

    override suspend fun on(event: E) {
        receiver.invoke(event)
    }
}

inline fun <reified E : Event> listener(
    noinline receiver: suspend E.() -> Unit
): Listener<E> = ReceiverListenerImpl(E::class, receiver)
