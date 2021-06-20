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
 * @author Zach Kozar
 * @version 6/19/2021
 */
@Experimental
class InteractionOptions(private val commandInteraction: ApplicationCommandInteraction) {

    inline operator fun <reified T> getValue(thisRef: Any?, property: KProperty<*>): T {
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

    fun search(name: String): ApplicationCommandInteractionOption? =
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
}