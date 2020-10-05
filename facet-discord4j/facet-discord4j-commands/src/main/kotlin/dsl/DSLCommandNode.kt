package io.facet.discord.commands.dsl

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.*
import com.mojang.brigadier.context.*
import com.mojang.brigadier.suggestion.*
import com.mojang.brigadier.tree.*

interface DSLCommandNode<S> {

    /**
     * Returns the brigardier node (only works after setup f.e on command execute)
     * @throws UninitializedPropertyAccessException if you run before or at setup
     */
    val node: CommandNode<S>

    /**
     * Just the brigadier builder
     */
    val builder: ArgumentBuilder<S, *>

    /**
     * Adds and Alias which can be used instead of the name of the Literal
     * @param alias the alias which should be added
     * @throws IllegalStateException if an alias is set and this node is an argument
     */
    fun alias(alias: String)

    /**
     * This will be called to check is there something required
     * runs on each request
     * false -> command will be hidden
     * true(default) -> command will show normally
     * @return is the command usable in this conditions
     */
    fun require(onCheck: suspend S.() -> Boolean)

    /**
     * this will be executed if the currend command is called
     * @return the result as int
     */
    fun executes(executed: suspend S.(context: CommandContext<S>) -> Int)

    /**
     * adds an Literal (non argument/subcommand) node
     * @see LiteralArgumentBuilder
     */
    fun literal(name: String, setupBlock: DSLCommandNode<S>.() -> Unit)

    /**
     * adds an argument node
     */
    fun <T> argument(name: String, type: ArgumentType<T>, setupBlock: DSLCommandNode<S>.() -> Unit)

    /**
     * adds an Suggestion provider
     * will run an each suggestion request
     */
    fun suggest(onSuggest: SuggestionsBuilder.(context: CommandContext<S>) -> Unit)
}
