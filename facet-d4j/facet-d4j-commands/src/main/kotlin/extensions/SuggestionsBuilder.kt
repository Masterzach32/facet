package io.facet.discord.commands.extensions

import com.mojang.brigadier.suggestion.*

public operator fun SuggestionsBuilder.plus(suggestion: String): SuggestionsBuilder = suggest(suggestion)

public operator fun SuggestionsBuilder.plus(suggestion: Int): SuggestionsBuilder = suggest(suggestion)