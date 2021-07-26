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

/**
 * Key for an object that allows for type-safe code.
 */
public class AttributeKey<T : Any>(public val name: String) : Comparable<AttributeKey<T>> {

    override fun toString(): String = name

    override fun compareTo(other: AttributeKey<T>): Int = name.compareTo(other.name)
}
