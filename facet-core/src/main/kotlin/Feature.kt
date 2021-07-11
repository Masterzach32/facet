package io.facet.core

import kotlinx.coroutines.*

/**
 * A feature is any code that can be installed into an object to improve its functionality.
 */
public abstract class Feature<in A, out C : Any, F : Any>(
    keyName: String,
    public val requiredFeatures: List<Feature<*, *, *>> = emptyList()
) {

    public val key: AttributeKey<F> = AttributeKey<F>(keyName)

    public fun checkRequiredFeatures(): Unit = requiredFeatures
            .map { it.key }
            .filterNot { Features.containsKey(it) }
            .map { it.name }
            .let {
                if (it.isNotEmpty())
                    error("Could not install feature: ${key.name}. Missing required features: $it")
            }

    /**
     * Feature installation script
     */
    public abstract suspend fun A.install(scope: CoroutineScope, configuration: C.() -> Unit): F
}
