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

package io.facet.discord.appcommands

import discord4j.common.annotations.*
import discord4j.core.`object`.command.*
import discord4j.rest.util.*
import io.facet.core.extensions.*
import kotlin.reflect.*

/*
 * facet - Created on 6/19/2021
 * Author: Zach Kozar
 * 
 * This code is licensed under the GNU AGPL v3
 * You can find more info in the LICENSE file at the project root.
 */

/**
 * An experimental class for getting command options through property delegation.
 *
 * @author Zach Kozar
 * @version 6/19/2021
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

    public inner class DefaultValueOptionDelegate<T>(private val value: T) {

        public operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return if (property.name in this@InteractionOptions)
                this@InteractionOptions.getValue(thisRef, property)
            else
                value
        }
    }

    public inner class NullableOptionDelegate<T> {

        public operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return if (property.name in this@InteractionOptions)
                this@InteractionOptions.getValue(thisRef, property)
            else
                null
        }
    }
}
