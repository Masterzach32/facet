package io.facet.core.extensions

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
public fun <T> Flow<T>.mergeWith(other: Flow<T>): Flow<T> = merge(this, other)
