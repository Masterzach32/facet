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

import com.mojang.brigadier.builder.*
import com.mojang.brigadier.context.*
import kotlinx.coroutines.*

@Deprecated("Use CommandNode DSL")
public fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.executesSuspend(
    command: suspend (context: CommandContext<S>) -> Unit
): T {
    return executes(object : SuspendCommand<S> {
        override suspend fun runSuspend(context: CommandContext<S>): Int {
            return command(context).let { 1 }
        }
    })
}

@Deprecated("Use CommandNode DSL")
public fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.requiresSuspend(
    requirement: suspend (source: S) -> Boolean
): T {
    return requires {
        runBlocking {
            requirement(it)
        }
    }
}
