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

package io.facet.discord.extensions

import discord4j.core.*
import discord4j.core.event.domain.*
import io.facet.core.*
import io.facet.discord.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*
import reactor.core.publisher.*

/**
 * Gets the currently installed [Feature] instance, if present.
 */
@Suppress("UNCHECKED_CAST")
public fun <F : Any> GatewayDiscordClient.featureOrNull(
    feature: Feature<*, *, F>
): F? = Features[feature.key]?.let { it as F }

/**
 * Gets the currently installed [Feature] instance, if present. Throws an IllegalStateException if the
 * requested feature is not installed.
 */
public fun <F : Any> GatewayDiscordClient.feature(
    feature: Feature<*, *, F>
): F = featureOrNull(feature)
    ?: error("Feature with key ${feature.key} has not been installed into this DiscordClient instance!")

/**
 * Installs a [Feature] into the [DiscordClient]. The feature is immediately set up, and any event
 * listeners are registered. If applicable, the feature can be configured using the config block.
 */
@ObsoleteCoroutinesApi
public suspend fun <C : Any> GatewayDiscordClient.install(
    scope: CoroutineScope,
    feature: GatewayFeature<C, *>,
    config: C.() -> Unit = {}
) {
    feature.checkRequiredFeatures()
    Features[feature.key] = with(feature) { install(scope, config) }
}

/**
 * Helper function to make listening to discord events shorter.
 */
public inline fun <reified E : Event> GatewayDiscordClient.on(): Flux<E> = on(E::class.java)

/**
 * Returns a [Flow] of the specified event type.
 */
public inline fun <reified E : Event> GatewayDiscordClient.flowOf(): Flow<E> = on<E>().asFlow()

