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
