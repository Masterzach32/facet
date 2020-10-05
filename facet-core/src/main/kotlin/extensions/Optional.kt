package io.facet.core.extensions

import java.util.*

/**
 * Extension function to convert a nullable type to an optional.
 */
fun <T : Any> T?.toOptional() = Optional.ofNullable(this)

@Deprecated("Use .value", ReplaceWith("value"))
fun <T : Any> Optional<T>.grab(): T? = orElse(null)

val <T : Any> Optional<T>.value: T?
    get() = orElse(null)
