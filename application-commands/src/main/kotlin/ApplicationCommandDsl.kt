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

import discord4j.discordjson.json.ApplicationCommandOptionChoiceData
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.util.ApplicationCommandOptionType

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

    public fun option(
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

    public fun subCommand(
        name: String,
        desc: String,
        block: OptionBuilder.() -> Unit = {}
    ): Unit = option(name, desc, ApplicationCommandOptionType.SUB_COMMAND, block = block)

    public fun group(
        name: String,
        desc: String,
        block: OptionBuilder.() -> Unit = {}
    ): Unit = option(name, desc, ApplicationCommandOptionType.SUB_COMMAND_GROUP, block = block)

    public fun string(name: String, desc: String, required: Boolean = false): Unit =
        option(name, desc, ApplicationCommandOptionType.STRING, required)

    public fun int(name: String, desc: String, required: Boolean = false): Unit =
        option(name, desc, ApplicationCommandOptionType.INTEGER, required)

    public fun boolean(name: String, desc: String, required: Boolean = false): Unit =
        option(name, desc, ApplicationCommandOptionType.BOOLEAN, required)

    public fun user(name: String, desc: String, required: Boolean = false): Unit =
        option(name, desc, ApplicationCommandOptionType.USER, required)

    public fun channel(name: String, desc: String, required: Boolean = false): Unit =
        option(name, desc, ApplicationCommandOptionType.CHANNEL, required)

    public fun role(name: String, desc: String, required: Boolean = false): Unit =
        option(name, desc, ApplicationCommandOptionType.ROLE, required)

    public fun mentionable(name: String, desc: String, required: Boolean = false): Unit =
        option(name, desc, ApplicationCommandOptionType.MENTIONABLE, required)
}

public class OptionBuilder : ApplicationCommandBuilder() {

    internal val choices = mutableListOf<ApplicationCommandOptionChoiceData>()

    public fun choice(name: String, value: String): Unit = choices.add(
        ApplicationCommandOptionChoiceData.builder()
            .name(name)
            .value(value)
            .build()
    ).let { }

    public fun choice(name: String, value: Int): Unit = choices.add(
        ApplicationCommandOptionChoiceData.builder()
            .name(name)
            .value(value)
            .build()
    ).let { }
}
