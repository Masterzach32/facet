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
public fun applicationCommandRequest(
    name: String,
    desc: String,
    block: ApplicationCommandBuilder.() -> Unit = {}
): ApplicationCommandRequest {
    val data = ApplicationCommandRequest.builder()

    val builder = ApplicationCommandBuilder().apply(block)

    if (builder.options.isNotEmpty())
        data.options(builder.options)

    return data.name(name).description(desc).defaultPermission(builder.defaultPermission).build()
}

public open class ApplicationCommandBuilder {

    public var defaultPermission: Boolean = true

    internal val options = mutableListOf<ApplicationCommandOptionData>()

    public fun addOption(
        name: String,
        desc: String,
        type: ApplicationCommandOptionType,
        required: Boolean = false,
        block: OptionBuilder.() -> Unit = {}
    ) {
        val data = ApplicationCommandOptionData.builder()

        val builder = OptionBuilder().apply(block)

        if (required)
            data.required(required)

        if (builder.options.isNotEmpty())
            data.options(builder.options)
        if (builder.choices.isNotEmpty())
            data.choices(builder.choices)

        options.add(data.name(name).description(desc).type(type.value).build())
    }

    public fun addSubCommand(
        name: String,
        desc: String,
        block: OptionBuilder.() -> Unit = {}
    ): Unit = addOption(name, desc, ApplicationCommandOptionType.SUB_COMMAND, block = block)

    public fun addSubCommandGroup(
        name: String,
        desc: String,
        block: OptionBuilder.() -> Unit = {}
    ): Unit = addOption(name, desc, ApplicationCommandOptionType.SUB_COMMAND_GROUP, block = block)
}

public class OptionBuilder : ApplicationCommandBuilder() {

    internal val choices = mutableListOf<ApplicationCommandOptionChoiceData>()

    public fun addChoice(name: String, value: String): Unit = choices.add(
        ApplicationCommandOptionChoiceData.builder()
            .name(name)
            .value(value)
            .build()
    ).let {  }

    public fun addChoice(name: String, value: Int): Unit = choices.add(
        ApplicationCommandOptionChoiceData.builder()
            .name(name)
            .value(value)
            .build()
    ).let {  }
}