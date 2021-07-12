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

public class CommandUsage(usageMap: Map<String, String>) : Iterable<CommandUsage.Pair> {

    private val usageMap = usageMap.map { (example, desc) -> Pair(example, desc) }

    override fun iterator(): Iterator<Pair> = usageMap.iterator()

    public data class Pair(val example: String, val description: String)
}

public class CommandUsageBuilder {

    internal val usageMap = mutableMapOf<String, String>()

    public fun default(description: String) {
        usageMap[""] = description
    }

    public fun add(example: String, description: String) {
        usageMap[example] = description
    }
}

/**
 * **Note:** this DSL is experimental and subject to change.
 */
public fun commandUsage(block: CommandUsageBuilder.() -> Unit): CommandUsage = CommandUsage(CommandUsageBuilder().apply(block).usageMap)
