/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.facet.core

import discord4j.core.*
import discord4j.core.event.*
import discord4j.core.shard.*
import discord4j.gateway.*
import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.*

public abstract class EventDispatcherFeature<out TConfig : Any, TFeature : Any>(
    keyName: String,
    requiredFeatures: List<Feature<*, *, *>> = emptyList()
) : Feature<EventDispatcher, TConfig, TFeature>(keyName, requiredFeatures)

/**
 * Gets the currently installed [Feature] instance, if present.
 */
@Suppress("UNCHECKED_CAST")
public fun <F : Any> EventDispatcher.featureOrNull(
    feature: EventDispatcherFeature<*, F>
): F? = Features[feature.key]?.let { it as F }

/**
 * Gets the currently installed [Feature] instance, if present. Throws an IllegalStateException if the
 * requested feature is not installed.
 */
public fun <F : Any> EventDispatcher.feature(
    feature: EventDispatcherFeature<*, F>
): F = featureOrNull(feature)
    ?: error("Feature with key ${feature.key} has not been installed into this DiscordClient instance!")

/**
 * Installs a [Feature] into the [DiscordClient]. The feature is immediately set up, and any event
 * listeners are registered. If applicable, the feature can be configured using the config block.
 */
@ObsoleteCoroutinesApi
public suspend fun <C : Any> EventDispatcher.install(
    scope: CoroutineScope,
    feature: EventDispatcherFeature<C, *>,
    config: C.() -> Unit = {}
) {
    feature.checkRequiredFeatures()
    Features[feature.key] = with(feature) { install(scope, config) }
}

/**
 * Configures the event dispatcher before any events can be received.
 */
public fun GatewayBootstrap<GatewayOptions>.withPlugins(
    configureBlock: suspend EventDispatcher.(CoroutineScope) -> Unit
): GatewayBootstrap<GatewayOptions> = withEventDispatcher { dispatcher ->
    mono {
        configureBlock(dispatcher, this)
    }
}
