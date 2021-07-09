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
class InteractionOptions(private val commandInteraction: ApplicationCommandInteraction) {

    @Suppress("UNCHECKED_CAST")
    operator fun <T> getValue(thisRef: Any?, property: KProperty<*>): T {
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

    fun <T> defaultValue(value: T) = DefaultValueOptionDelegate(value)

    fun <T> nullable() = NullableOptionDelegate<T>()

    operator fun contains(name: String): Boolean = search(name) != null

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

    inner class DefaultValueOptionDelegate<T>(private val value: T) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return if (property.name in this@InteractionOptions)
                this@InteractionOptions.getValue(thisRef, property)
            else
                value
        }
    }

    inner class NullableOptionDelegate<T> {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return if (property.name in this@InteractionOptions)
                this@InteractionOptions.getValue(thisRef, property)
            else
                null
        }
    }
}
