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

package io.facet.common

import java.util.*

/**
 * Extension function to convert a nullable type to an optional.
 */
public fun <T : Any> T?.toOptional(): Optional<T> = Optional.ofNullable(this)

@Deprecated("Use unwrap()", ReplaceWith("unwrap()"), level = DeprecationLevel.ERROR)
public fun <T : Any> Optional<T>.grab(): T? = unwrap()

@Deprecated("Use unwrap()", ReplaceWith("unwrap()"), level = DeprecationLevel.ERROR)
public val <T : Any> Optional<T>.value: T?
    get() = unwrap()

/**
 * Unwraps an Optional<T> to a nullable T?.
 */
public fun <T> Optional<T>.unwrap(): T? = orElse(null)
