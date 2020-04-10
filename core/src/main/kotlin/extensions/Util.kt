package io.discordcommons.core.extensions

inline fun Any?.ifNull(block: () -> Unit) {
    if (this == null)
        block()
}
