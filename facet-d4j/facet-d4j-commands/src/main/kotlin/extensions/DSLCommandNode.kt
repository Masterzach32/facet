package io.facet.discord.commands.extensions

import com.mojang.brigadier.context.*
import io.facet.discord.commands.dsl.*

/**
 * Like executes but always return 0, so there is no need to return.
 * @see DSLCommandNode.executes
 */
public fun <T> DSLCommandNode<T>.runs(executed: suspend T.(context: CommandContext<T>) -> Unit) {
    executes { executed.invoke(this, it); return@executes 0 }
}
