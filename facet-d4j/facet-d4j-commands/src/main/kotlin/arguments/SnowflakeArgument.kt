package io.facet.discord.commands.arguments

import com.mojang.brigadier.*
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.arguments.LongArgumentType.*
import discord4j.common.util.*
import io.facet.discord.extensions.*

public object SnowflakeArgument : ArgumentType<Snowflake> {

    private val longArgType = longArg(0, Long.MAX_VALUE)

    override fun parse(reader: StringReader): Snowflake = longArgType.parse(reader).toSnowflake()
}
