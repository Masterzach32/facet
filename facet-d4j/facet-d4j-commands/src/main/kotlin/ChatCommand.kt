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

package io.facet.discord.commands

import com.mojang.brigadier.*
import discord4j.rest.util.*
import io.facet.discord.commands.dsl.*
import io.facet.discord.commands.extensions.*

public abstract class ChatCommand(
    public val name: String,
    public val aliases: Set<String>,
    public val scope: Scope = Scope.ALL,
    public val category: String = "none",
    public val description: String? = null,
    public val discordPermsRequired: PermissionSet = PermissionSet.none(),
    public val usage: CommandUsage? = null
) {

    protected abstract fun DSLCommandNode<ChatCommandSource>.register()

    internal fun register(dispatcher: CommandDispatcher<ChatCommandSource>) {
        require(aliases.isNotEmpty()) { "Command $name must have at least one alias!" }

        dispatcher.literal(aliases.first()) {
            aliases.drop(1).forEach { alias(it) }

            register()
        }
    }
}
