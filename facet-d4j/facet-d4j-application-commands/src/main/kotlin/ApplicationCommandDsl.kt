package io.facet.discord.appcommands

import discord4j.discordjson.json.*
import discord4j.rest.util.*

/*
 * facet - Created on 6/15/2021
 * Author: Zach Kozar
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at the project root.
 */

/**
 * @author Zach Kozar
 * @version 6/15/2021
 */

/**
 * DSL for building [ApplicationCommandRequest]
 */
fun applicationCommandRequest(
    name: String,
    desc: String,
    block: ApplicationCommandBuilder.() -> Unit = {}
): ApplicationCommandRequest {
    val data = ApplicationCommandRequest.builder()

    val builder = ApplicationCommandBuilder().apply(block)

    if (builder.options.isNotEmpty())
        data.options(builder.options)

    return data.name(name).description(desc).build()
}

open class ApplicationCommandBuilder {

    val options = mutableListOf<ApplicationCommandOptionData>()

    fun addOption(
        name: String,
        desc: String,
        type: ApplicationCommandOptionType,
        required: Boolean? = null,
        block: OptionBuilder.() -> Unit = {}
    ) {
        val data = ApplicationCommandOptionData.builder()

        val builder = OptionBuilder().apply(block)

        if (required != null)
            data.required(required)

        if (builder.options.isNotEmpty())
            data.options(builder.options)
        if (builder.choices.isNotEmpty())
            data.choices(builder.choices)

        options.add(data.name(name).description(desc).type(type.value).build())
    }

    fun addSubCommand(
        name: String,
        desc: String,
        block: OptionBuilder.() -> Unit = {}
    ) = addOption(name, desc, ApplicationCommandOptionType.SUB_COMMAND, block = block)

    fun addSubCommandGroup(
        name: String,
        desc: String,
        block: OptionBuilder.() -> Unit = {}
    ) = addOption(name, desc, ApplicationCommandOptionType.SUB_COMMAND_GROUP, block = block)
}

class OptionBuilder : ApplicationCommandBuilder() {

    val choices = mutableListOf<ApplicationCommandOptionChoiceData>()

    fun addChoice(name: String, value: String) = choices.add(
        ApplicationCommandOptionChoiceData.builder()
            .name(name)
            .value(value)
            .build()
    )

    fun addChoice(name: String, value: Int) = choices.add(
        ApplicationCommandOptionChoiceData.builder()
            .name(name)
            .value(value)
            .build()
    )
}
