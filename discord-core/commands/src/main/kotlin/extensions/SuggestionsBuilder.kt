package io.facet.discord.commands.extensions

import com.mojang.brigadier.suggestion.*

operator fun SuggestionsBuilder.plus(suggestion: String): SuggestionsBuilder = suggest(suggestion)

operator fun SuggestionsBuilder.plus(suggestion: Int): SuggestionsBuilder = suggest(suggestion)