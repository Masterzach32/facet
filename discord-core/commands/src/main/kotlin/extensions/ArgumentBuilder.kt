package io.facet.discord.commands.extensions

import com.mojang.brigadier.builder.*
import com.mojang.brigadier.context.*
import io.facet.discord.commands.*
import kotlinx.coroutines.*

fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.executesSuspend(
    command: suspend (context: CommandContext<S>) -> Unit
): T {
    return executes(object : SuspendCommand<S> {
        override suspend fun runSuspend(context: CommandContext<S>): Int {
            return command(context).let { 1 }
        }
    })
}

fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.requiresSuspend(
    requirement: suspend (source: S) -> Boolean
): T {
    return requires {
        runBlocking {
            requirement(it)
        }
    }
}
