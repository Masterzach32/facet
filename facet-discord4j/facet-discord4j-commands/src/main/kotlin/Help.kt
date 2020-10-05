package io.facet.discord.commands

import com.mojang.brigadier.arguments.*
import discord4j.rest.util.*
import io.facet.core.extensions.*
import io.facet.discord.commands.dsl.*
import io.facet.discord.commands.extensions.*
import io.facet.discord.extensions.*

object Help : ChatCommand(
    name = "Help",
    aliases = setOf("help", "h"),
    description = "Get a list of available commands, or more information about a specific command.",
    usage = commandUsage {
        default("Replies with a list of commands.")
        add("<command>", "Replies with specific information about the specified command.")
    }
) {

    override fun DSLCommandNode<ChatCommandSource>.register() {
        runs {
            val feature = client.feature(ChatCommands)
            val commandsByCategory = feature.commands.groupBy(ChatCommand::category)
            val commandPrefix = feature.commandPrefixFor(guildId.toOptional())

            getChannel().createEmbedReceiver {
                setColor(Color.LIGHT_GRAY)
                setTitle("Help and Info")
                setDescription(
                    """
                    To view more information for a command, use `${commandPrefix}help <command>`
                    ${if (guildId == null) "**Note:** Command prefixes may be different per server!" else ""}
                """.trimIndent()
                )

                commandsByCategory.forEach { (category, subCommands) ->
                    val commandList = subCommands.joinToString(separator = "\n") {
                        "${commandPrefix}${it.aliases.first()}"
                    }
                    addField(category, commandList, true)
                }
            }.awaitComplete()
        }

        argument("command", StringArgumentType.word()) {
            runs { context ->
                val cmdAlias = context.getString("command").toLowerCase()
                val feature = client.feature(ChatCommands)
                val command = feature.commandMap.entries.firstOrNull { (alias, _) -> cmdAlias == alias }?.value
                val prefix = feature.commandPrefixFor(guildId.toOptional())

                if (command != null) {
                    getChannel().createEmbedReceiver {
                        setColor(Color.LIGHT_GRAY)
                        setTitle("Command: **${command.name}**")
                        setDescription(command.description ?: "No description.")

                        if (command.usage != null) {
                            val usageText = command.usage.joinToString(separator = "\n") { (example, desc) ->
                                "`${prefix}${cmdAlias} ${example}` $desc"
                            }
                            addField("Usage", usageText, false)
                        }

                        addField("Aliases", command.aliases.toString(), true)
                        addField("Scope", command.scope.toString(), true)
                        addField("Permissions", command.discordPermsRequired.toString(), true)
                    }.awaitComplete()
                } else {
                    getChannel().createMessage("Could not find command: `${cmdAlias}`").awaitComplete()
                }
            }
        }
    }
}
