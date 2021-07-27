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

package io.facet.chatcommands

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.*
import com.mojang.brigadier.context.*
import com.mojang.brigadier.suggestion.*
import com.mojang.brigadier.tree.*
import io.facet.chatcommands.*
import kotlinx.coroutines.*

internal class DSLCommandNodeImpl<S>(
    override val builder: ArgumentBuilder<S, *>,
    private val parent: CommandNode<S>
) : DSLCommandNode<S> {

    val aliases = mutableSetOf<String>()
    var requires: suspend (source: S) -> Boolean = { true }
    var executing: (suspend (context: CommandContext<S>) -> Unit)? = null
    val children = mutableSetOf<CommandEntry<S>>()
    private var suggestionProvider: SuggestionProvider<S>? = null

    override lateinit var node: CommandNode<S>

    override fun alias(alias: String) {
        if (this.builder !is LiteralArgumentBuilder<S>) error("Only literals can have aliases!")
        aliases.add(alias)
    }

    override fun require(onCheck: suspend S.() -> Boolean) {
        requires = { onCheck.invoke(it) }
    }

    override fun executes(executed: suspend S.(context: CommandContext<S>) -> Int) {
        executing = { context -> executed.invoke(context.source, context) }
    }

    override fun literal(name: String, setupBlock: DSLCommandNode<S>.() -> Unit) {
        children.add(CommandEntry(LiteralArgumentBuilder.literal(name), setupBlock))
    }

    override fun <T> argument(name: String, type: ArgumentType<T>, setupBlock: DSLCommandNode<S>.() -> Unit) {
        children.add(CommandEntry(RequiredArgumentBuilder.argument(name, type), setupBlock))
    }

    override fun suggest(onSuggest: SuggestionsBuilder.(context: CommandContext<S>) -> Unit) {
        if (this.builder !is RequiredArgumentBuilder<S, *>) error("Only Arguments can have Custom Suggestions!")
        suggestionProvider = DSLSuggestionProvider(onSuggest)
    }

    internal fun buildTree() {
        node = builder.also { build ->
            modBuilder(build)
        }.build()
        children.forEach { entry ->
            val dslNode = DSLCommandNodeImpl(entry.argumentBuilder, node)
            entry.setupBlock.invoke(dslNode)
            dslNode.buildTree()
            node.addChild(dslNode.node)
        }

        aliases.forEach { alias ->
            val aliasnode = LiteralArgumentBuilder.literal<S>(alias)
                .fork(node, null).also {
                    modBuilder(it)
                }.build()
            parent.addChild(aliasnode)
        }

    }

    private fun modBuilder(builder: ArgumentBuilder<S, *>) {
        builder.requires { runBlocking { requires.invoke(it) } }
        builder.executes(object : SuspendCommand<S> {
            override suspend fun runSuspend(context: CommandContext<S>): Int {
                executing?.invoke(context)
                return 1
            }
        })
        if (suggestionProvider != null && builder is RequiredArgumentBuilder<S, *>) {
            builder.suggests(suggestionProvider)
        }
    }

    class CommandEntry<S>(val argumentBuilder: ArgumentBuilder<S, *>, val setupBlock: DSLCommandNode<S>.() -> Unit)
}
