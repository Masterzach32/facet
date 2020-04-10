package io.discordcommons.core.extensions

import java.util.*

fun <T : Any> T?.toOptional() = Optional.ofNullable(this)

fun <T : Any> Optional<T>.getOrNull(): T? = if (isPresent) get() else null
