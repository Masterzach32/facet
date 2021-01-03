package io.facet.core

import kotlinx.coroutines.*

/**
 * A feature is any code that can be installed into an object to improve its functionality.
 */
abstract class Feature<in TApplication, out TConfiguration : Any, TFeature : Any>(
    keyName: String,
    val requiredFeatures: List<Feature<*, *, *>> = emptyList()
) {

    val key = AttributeKey<TFeature>(keyName)

    fun checkRequiredFeatures() = requiredFeatures
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
    abstract fun TApplication.install(scope: CoroutineScope, configuration: TConfiguration.() -> Unit): TFeature
}
