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

import com.mojang.brigadier.context.*
import com.mojang.brigadier.suggestion.*
import kotlinx.coroutines.*
import java.util.concurrent.*

internal class DSLSuggestionProvider<T>(
    private val onSuggest: SuggestionsBuilder.(context: CommandContext<T>) -> Unit
) : SuggestionProvider<T> {

    override fun getSuggestions(
        context: CommandContext<T>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        runBlocking {
            onSuggest.invoke(builder, context)
        }
        return builder.buildFuture()
    }
}
