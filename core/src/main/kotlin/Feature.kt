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

import kotlinx.coroutines.*

/**
 * A feature is any code that can be installed into an object to improve its functionality.
 */
public abstract class Feature<in A, out C : Any, F : Any>(
    keyName: String,
    public val requiredFeatures: List<Feature<*, *, *>> = emptyList()
) {

    public val key: AttributeKey<F> = AttributeKey(keyName)

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
