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

package io.facet.chatcommands.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import discord4j.core.`object`.entity.Entity

public class GuildEntityArgument<E : Entity, T : EntitySelector<E>> internal constructor(
    private val selector: T
) : ArgumentType<T> {

    override fun parse(reader: StringReader): T {
        return selector.apply { parse(reader) }
    }

    override fun getExamples(): MutableCollection<String> {
        return Companion.examples.toMutableList()
    }

    private companion object {
        val examples = listOf("Wumpus", "@Wumpus", "@role", "#text-channel", "\"Games Voice\"", "999999999999999999")
    }
}
