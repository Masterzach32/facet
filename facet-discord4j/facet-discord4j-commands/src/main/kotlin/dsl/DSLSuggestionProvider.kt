package io.facet.discord.commands.dsl

import com.mojang.brigadier.context.*
import com.mojang.brigadier.suggestion.*
import kotlinx.coroutines.*
import java.util.concurrent.*

internal class DSLSuggestionProvider<T>(
    val onSuggest: SuggestionsBuilder.(context: CommandContext<T>) -> Unit
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
