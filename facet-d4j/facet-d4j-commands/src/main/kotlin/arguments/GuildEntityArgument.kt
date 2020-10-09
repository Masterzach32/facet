package io.facet.discord.commands.arguments

import com.mojang.brigadier.*
import com.mojang.brigadier.arguments.*
import discord4j.core.`object`.entity.*

class GuildEntityArgument<E : Entity, T : EntitySelector<E>> internal constructor(
    private val selector: T
) : ArgumentType<T> {

    override fun parse(reader: StringReader): T {
        return selector.apply { parse(reader) }
    }

    override fun getExamples(): MutableCollection<String> {
        return Companion.examples.toMutableList()
    }

    companion object {
        val examples = listOf("Wumpus", "@Wumpus", "@role", "#text-channel", "\"Games Voice\"", "999999999999999999")
    }
}
