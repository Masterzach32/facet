package io.facet.core.extensions

import java.util.*

/**
 * Extension function to convert a nullable type to an optional.
 */
public fun <T : Any> T?.toOptional(): Optional<T> = Optional.ofNullable(this)

@Deprecated("Use unwrap()", ReplaceWith("unwrap()"))
public fun <T : Any> Optional<T>.grab(): T? = unwrap()

@Deprecated("Use unwrap()", ReplaceWith("unwrap()"))
public val <T : Any> Optional<T>.value: T?
    get() = unwrap()

/**
 * Unwraps an Optional<T> to a nullable T?.
 */
public fun <T> Optional<T>.unwrap(): T? = orElse(null)
