package io.facet.discord.commands

import com.mojang.brigadier.*
import com.mojang.brigadier.context.*

interface SuspendCommand<S> : Command<S> {

    suspend fun runSuspend(context: CommandContext<S>): Int

    override fun run(context: CommandContext<S>): Int {
        return 0
    }
}