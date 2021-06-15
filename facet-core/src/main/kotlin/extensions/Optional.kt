package io.facet.core.extensions

import java.util.*

/**
 * Extension function to convert a nullable type to an optional.
 */
fun <T : Any> T?.toOptional() = Optional.ofNullable(this)

@Deprecated("Use unwrap()", ReplaceWith("unwrap()"))
fun <T : Any> Optional<T>.grab(): T? = unwrap()

@Deprecated("Use unwrap()", ReplaceWith("unwrap()"))
val <T : Any> Optional<T>.value: T?
    get() = unwrap()

/**
 * Unwraps an Optional<T> to a nullable T?.
 */
fun <T> Optional<T>.unwrap(): T? = orElse(null)
