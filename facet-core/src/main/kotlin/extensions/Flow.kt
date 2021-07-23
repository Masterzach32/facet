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

package io.facet.core.extensions

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Merge two flows together.
 */
@ExperimentalCoroutinesApi
public fun <T> Flow<T>.mergeWith(other: Flow<T>): Flow<T> = merge(this, other)

/**
 * Returns a flow which only emits unique values of type T.
 */
public fun <T> Flow<T>.distinct(): Flow<T> = distinctBy { it }

/**
 * Returns a flow which only emits unique values determined by the given selector function.
 */
public fun <T, K> Flow<T>.distinctBy(selector: (T) -> K): Flow<T> = flow {
    val keySet = mutableSetOf<K>()
    collect { value ->
        if (keySet.add(selector(value)))
            emit(value)
    }
}
