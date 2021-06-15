package io.facet.discord.appcommands

import discord4j.discordjson.json.*
import discord4j.rest.util.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

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
class DslTest {

    @Test
    fun `check dsl builder`() {
        val name = "test"
        val desc = "A test command"
        val optionName = "option1"
        val optionDesc = "Option 1"
        val optionType = ApplicationCommandOptionType.STRING
        val optionRequired = true
        val choiceName = "Choice 1"
        val choiceValue = "choice1"

        val fromDsl = applicationCommandRequest(name, desc) {
            addOption(optionName, optionDesc, optionType, optionRequired) {
                addChoice(choiceName, choiceValue)
            }
        }

        val fromBuilder = ApplicationCommandRequest.builder()
            .name(name)
            .description(desc)
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

        assertEquals(fromBuilder, fromDsl, "Dsl request should match builder request")
    }

}
