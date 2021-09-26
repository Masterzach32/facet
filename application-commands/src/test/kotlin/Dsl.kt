package io.facet.discord.appcommands

import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import io.facet.commands.applicationCommandRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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
class Dsl {

    @Test
    fun `check dsl builder`() {
        val name = "test"
        val desc = "A test command"
        val subCommandName = "subcommand"
        val subCommandDesc = "A sub command"
        val optionName = "option1"
        val optionDesc = "Option 1"
        val optionType = ApplicationCommandOption.Type.STRING
        val optionRequired = true
        val choiceName = "Choice 1"
        val choiceValue = "choice1"

        val fromDsl = applicationCommandRequest(name, desc) {
            defaultPermission = true
            subCommand(subCommandName, subCommandDesc) {
                option(optionName, optionDesc, optionType, optionRequired) {
                    choice(choiceName, choiceValue)
                }
            }
        }

        val fromBuilder = ApplicationCommandRequest.builder()
            .name(name)
            .description(desc)
            .addOption(
                ApplicationCommandOptionData.builder()
                    .name(subCommandName)
                    .description(subCommandDesc)
                    .type(ApplicationCommandOption.Type.SUB_COMMAND.value)
                    .addOption(
                        ApplicationCommandOptionData.builder()
                            .name(optionName)
                            .description(optionDesc)
                            .type(optionType.value)
                            .required(optionRequired)
                            .addChoice(
                                ApplicationCommandOptionChoiceData.builder()
                                    .name(choiceName)
                                    .value(choiceValue)
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .defaultPermission(true)
            .build()

        assertEquals(fromBuilder, fromDsl, "Dsl request should match builder request")
    }

}
