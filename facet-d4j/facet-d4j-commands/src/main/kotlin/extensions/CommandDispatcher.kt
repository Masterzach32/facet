package io.facet.discord.commands.extensions

import com.mojang.brigadier.*
import com.mojang.brigadier.builder.*
import com.mojang.brigadier.context.*
import com.mojang.brigadier.exceptions.*
import com.mojang.brigadier.tree.*
import io.facet.discord.commands.*
import io.facet.discord.commands.dsl.*
import kotlinx.coroutines.*
import java.util.*

/**
 * adds an Literal (non argument/subcommand) node
 * @see LiteralArgumentBuilder
 */
fun <T> CommandDispatcher<T>.literal(name: String, setup: DSLCommandNode<T>.() -> Unit): CommandNode<T> {
    val literal = LiteralArgumentBuilder.literal<T>(name)
    val node = DSLCommandNodeImpl<T>(literal, this.root)
    setup.invoke(node)
    node.buildTree()
    this.root.addChild(node.node)
    return node.node
}

//suspend fun CommandDispatcher<ChatCommandSource>.parseSuspend(
//    command: String,
//    source: ChatCommandSource
//): ParseResults<ChatCommandSource> {}

suspend fun CommandDispatcher<ChatCommandSource>.executeSuspend(parseResults: ParseResults<ChatCommandSource>): Int {
    if (parseResults.getReader().canRead()) {
        if (parseResults.getExceptions().size == 1) {
            throw parseResults.getExceptions().values.iterator().next()
        } else if (parseResults.getContext().getRange().isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()
                .createWithContext(parseResults.getReader())
        } else {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()
                .createWithContext(parseResults.getReader())
        }
    }

    var result = 0
    var successfulForks = 0
    var forked = false
    var foundCommand = false
    val command: String = parseResults.getReader().getString()
    val original: CommandContext<ChatCommandSource> = parseResults.getContext().build(command)
    var contexts: List<CommandContext<ChatCommandSource>>? = listOf(original)
    var next: ArrayList<CommandContext<ChatCommandSource>>? = null

    while (contexts != null) {
        val size = contexts.size
        for (i in 0 until size) {
            val context: CommandContext<ChatCommandSource> = contexts[i]
            val child: CommandContext<ChatCommandSource>? = context.getChild()
            if (child != null) {
                forked = forked or context.isForked()
                if (child.hasNodes()) {
                    foundCommand = true
                    val modifier: RedirectModifier<ChatCommandSource>? = context.getRedirectModifier()
                    if (modifier == null) {
                        if (next == null) {
                            next = ArrayList<CommandContext<ChatCommandSource>>(1)
                        }
                        next.add(child.copyFor(context.getSource()))
                    } else {
                        try {
                            val results: Collection<ChatCommandSource> = modifier.apply(context)
                            if (!results.isEmpty()) {
                                if (next == null) {
                                    next = ArrayList<CommandContext<ChatCommandSource>>(results.size)
                                }
                                for (source in results) {
                                    next.add(child.copyFor(source))
                                }
                            }
                        } catch (ex: CommandSyntaxException) {
                            //consumer.onCommandComplete(context, false, 0)
                            if (!forked) {
                                throw ex
                            }
                        }
                    }
                }
            } else if (context.getCommand() != null) {
                foundCommand = true
                try {
                    val value = withContext(Dispatchers.Default) {
                        (context.command as? SuspendCommand<ChatCommandSource>)?.runSuspend(context)
                            ?: context.command.run(context)
                    }
                    result += value
                    //consumer.onCommandComplete(context, true, value)
                    successfulForks++
                } catch (ex: CommandSyntaxException) {
                    //consumer.onCommandComplete(context, false, 0)
                    if (!forked) {
                        throw ex
                    }
                }
            }
        }
        contexts = next
        next = null
    }

    if (!foundCommand) {
        //consumer.onCommandComplete(original, false, 0)
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults.getReader())
    }

    return if (forked) successfulForks else result
}