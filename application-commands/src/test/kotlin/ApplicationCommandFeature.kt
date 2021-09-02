package io.facet.discord.appcommands

import discord4j.discordjson.json.ApplicationCommandData
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.util.ApplicationCommandOptionType
import org.junit.jupiter.api.Test

class ApplicationCommandFeature {

    @Test
    fun `request to data comparison`() {
        val name = "test"
        val desc = "A test command"
        val subCommandName = "subcommand"
        val subCommandDesc = "A sub command"
        val optionName = "option1"
        val optionDesc = "Option 1"
        val optionType = ApplicationCommandOptionType.STRING
        val optionRequired = true
        val choiceName = "Choice 1"
        val choiceValue = "choice1"

        val request = ApplicationCommandRequest.builder()
            .name(name)
            .description(desc)
            .addOption(
                ApplicationCommandOptionData.builder()
                    .name(subCommandName)
                    .description(subCommandDesc)
                    .type(ApplicationCommandOptionType.SUB_COMMAND.value)
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
            .build()

        val actual = ApplicationCommandData.builder()
            .id("")
            .applicationId("")
            .name(name)
            .description(desc)
            .addOption(
                ApplicationCommandOptionData.builder()
                    .name(subCommandName)
                    .description(subCommandDesc)
                    .type(ApplicationCommandOptionType.SUB_COMMAND.value)
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
            .build()

        assert(
            request.name() == actual.name() && request.description() == actual.description() &&
                request.defaultPermission() == actual.defaultPermission() &&
                request.options() == actual.options()
        )
    }
}
