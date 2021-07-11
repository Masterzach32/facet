package io.facet.discord.commands

import com.mojang.brigadier.*
import com.mojang.brigadier.context.*

public interface SuspendCommand<S> : Command<S> {

    public suspend fun runSuspend(context: CommandContext<S>): Int

    override fun run(context: CommandContext<S>): Int {
        return 0
    }
}