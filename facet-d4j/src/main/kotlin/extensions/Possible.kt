package io.facet.discord.extensions

import discord4j.discordjson.possible.*

/**
 * Unwraps the D4J [Possible] into a nullable type.
 */
public fun <T> Possible<T>.unwrap(): T? = if (isAbsent) null else get()
