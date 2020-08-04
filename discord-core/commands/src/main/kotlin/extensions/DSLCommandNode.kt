package io.facet.discord.commands.extensions

import com.mojang.brigadier.context.*
import io.facet.discord.commands.dsl.*

/**
 * Like executes but always return 0!
 * so the is no return need
 * @see DSLCommandNode.executes
 */
fun <T> DSLCommandNode<T>.runs(executed: suspend T.(context: CommandContext<T>) -> Unit) {
    executes { executed.invoke(this, it); return@executes 0 }
}
