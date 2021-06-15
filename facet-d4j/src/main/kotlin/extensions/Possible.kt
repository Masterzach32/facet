package io.facet.discord.extensions

import discord4j.discordjson.possible.*

@Deprecated("Use unwrap()", ReplaceWith("unwrap()"))
val <T : Any> Possible<T>.nullable: T?
    get() = unwrap()

/**
 * Unwraps the D4J [Possible] into a nullable type.
 */
fun <T> Possible<T>.unwrap(): T? = if (isAbsent) null else get()
