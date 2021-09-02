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

package io.facet.commands

import discord4j.common.annotations.Experimental
import discord4j.core.`object`.command.ApplicationCommandInteraction
import discord4j.core.`object`.command.ApplicationCommandInteractionOption
import discord4j.rest.util.ApplicationCommandOptionType
import io.facet.common.unwrap
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * An experimental class for getting command options through property delegation.
 *
 * Example:
 *
 * ```kotlin
 * val name: String by options
 * val count: Int by options
 * val enabled: Boolean by options
 * val user: Mono<User> by options
 * val channel: Mono<Channel> by options
 * val role: Mono<Role> by options
 *
 * // also
 * val nullableName: String? by options.nullable()
 * val defaultName: String by options.defaultValue("test")
 * ```
 */
@Experimental
public class InteractionOptions(private val commandInteraction: ApplicationCommandInteraction) {

    @Suppress("UNCHECKED_CAST")
    public operator fun <T> getValue(thisRef: Any?, property: KProperty<*>): T {
        val option = search(property.name) ?: error("No property by name ${property.name}.")
        val optionValue = option.value.unwrap() ?: error("${property.name} was null.")

        return when (option.type) {
            ApplicationCommandOptionType.STRING -> optionValue.asString() as T
            ApplicationCommandOptionType.INTEGER -> optionValue.asLong() as T
            ApplicationCommandOptionType.BOOLEAN -> optionValue.asBoolean() as T
            ApplicationCommandOptionType.USER -> optionValue.asUser() as T
            ApplicationCommandOptionType.CHANNEL -> optionValue.asChannel() as T
            ApplicationCommandOptionType.ROLE -> optionValue.asRole() as T
            else -> error("Unsupported type: ${option.type}")
        }
    }

    public fun <T> defaultValue(value: T): DefaultValueOptionDelegate<T> = DefaultValueOptionDelegate(value)

    public fun <T> nullable(): NullableOptionDelegate<T> = NullableOptionDelegate()

    public operator fun contains(name: String): Boolean = search(name) != null

    private fun search(name: String): ApplicationCommandInteractionOption? =
        commandInteraction.options
            .firstOrNull { it.name == name }
            ?: commandInteraction.options
                .mapNotNull { search(name, it) }
                .firstOrNull()

    private fun search(name: String, node: ApplicationCommandInteractionOption): ApplicationCommandInteractionOption? =
        node.options
            .firstOrNull { it.name == name }
            ?: node.options
                .mapNotNull { search(name, it) }
                .firstOrNull()

    public inner class DefaultValueOptionDelegate<T>(private val value: T) : ReadOnlyProperty<Any?, T> {

        public override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return if (property.name in this@InteractionOptions)
                this@InteractionOptions.getValue(thisRef, property)
            else
                value
        }
    }

    public inner class NullableOptionDelegate<T> : ReadOnlyProperty<Any?, T?> {

        public override operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return if (property.name in this@InteractionOptions)
                this@InteractionOptions.getValue(thisRef, property)
            else
                null
        }
    }
}
