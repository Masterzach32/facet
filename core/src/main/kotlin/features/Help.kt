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

package io.facet.core.features

import com.mojang.brigadier.arguments.StringArgumentType
import discord4j.rest.util.Color
import io.facet.chatcommands.*
import io.facet.common.sendMessage
import io.facet.core.feature
import java.util.*

public object Help : ChatCommand(
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
            val commandPrefix = feature.commandPrefixFor(guildId)

            respondEmbed {
                color = Color.LIGHT_GRAY
                title = "Help and Info"
                description = """
                    To view more information for a command, use `${commandPrefix}help <command>`
                    ${if (guildId == null) "**Note:** Command prefixes may be different per server!" else ""}
                """.trimIndent()

                commandsByCategory.forEach { (category, subCommands) ->
                    val commandList = subCommands.joinToString(separator = "\n") {
                        "${commandPrefix}${it.aliases.first()}"
                    }
                    field(category, commandList, true)
                }
            }
        }

        argument("command", StringArgumentType.word()) {
            runs { context ->
                val cmdAlias = context.getString("command").lowercase(Locale.getDefault())
                val feature = client.feature(ChatCommands)
                val command = feature.commandMap.entries.firstOrNull { (alias, _) -> cmdAlias == alias }?.value
                val prefix = feature.commandPrefixFor(guildId)

                if (command != null) {
                    respondEmbed {
                        color = Color.LIGHT_GRAY
                        title = "Command: **${command.name}**"
                        description = command.description ?: "No description."

                        if (command.usage != null) {
                            val usageText = command.usage!!.joinToString(separator = "\n") { (example, desc) ->
                                "`${prefix}${cmdAlias} ${example}` $desc"
                            }
                            field("Usage", usageText, false)
                        }

                        field("Aliases", command.aliases.toString(), true)
                        field("Scope", command.scope.toString(), true)
                        field("Permissions", command.discordPermsRequired.toString(), true)
                        footer("<> = required | () = optional")
                    }
                } else {
                    getChannel().sendMessage("Could not find command: `${cmdAlias}`")
                }
            }
        }
    }
}
