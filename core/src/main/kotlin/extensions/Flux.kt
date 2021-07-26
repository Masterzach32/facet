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

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*
import reactor.core.publisher.*

/**
 * Suspends the current coroutine, and returns all elements emitted by the [Flux] in a [List].
 */
public suspend fun <T : Any> Flux<T>.await(): List<T> = asFlow().toList()

/**
 * Suspends the current coroutine, and returns when the [Flux] completes.
 */
@JvmName("awaitVoid")
public suspend fun Flux<Void>.await(): Unit = asFlow().collect()
