package io.facet.discord.extensions

import discord4j.discordjson.possible.*

val <T : Any> Possible<T>.nullable: T?
    get() = if (isAbsent) null else get()