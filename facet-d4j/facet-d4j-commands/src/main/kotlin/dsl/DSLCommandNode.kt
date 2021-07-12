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

package io.facet.discord.commands.dsl

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.*
import com.mojang.brigadier.context.*
import com.mojang.brigadier.suggestion.*
import com.mojang.brigadier.tree.*

public interface DSLCommandNode<S> {

    /**
     * Returns the brigardier node (only works after setup f.e on command execute)
     * @throws UninitializedPropertyAccessException if you run before or at setup
     */
    public val node: CommandNode<S>

    /**
     * Just the brigadier builder
     */
    public val builder: ArgumentBuilder<S, *>

    /**
     * Adds and Alias which can be used instead of the name of the Literal
     * @param alias the alias which should be added
     * @throws IllegalStateException if an alias is set and this node is an argument
     */
    public fun alias(alias: String)

    /**
     * This will be called to check is there something required
     * runs on each request
     * false -> command will be hidden
     * true(default) -> command will show normally
     * @return is the command usable in this conditions
     */
    public fun require(onCheck: suspend S.() -> Boolean)

    /**
     * this will be executed if the currend command is called
     * @return the result as int
     */
    public fun executes(executed: suspend S.(context: CommandContext<S>) -> Int)

    /**
     * adds an Literal (non argument/subcommand) node
     * @see LiteralArgumentBuilder
     */
    public fun literal(name: String, setupBlock: DSLCommandNode<S>.() -> Unit)

    /**
     * adds an argument node
     */
    public fun <T> argument(name: String, type: ArgumentType<T>, setupBlock: DSLCommandNode<S>.() -> Unit)

    /**
     * adds an Suggestion provider
     * will run an each suggestion request
     */
    public fun suggest(onSuggest: SuggestionsBuilder.(context: CommandContext<S>) -> Unit)
}
