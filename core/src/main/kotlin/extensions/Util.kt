package io.facet.core.extensions

inline fun Any?.ifNull(block: () -> Unit) {
    if (this == null)
        block()
}
