package io.facet.discord.commands.extensions

import com.mojang.brigadier.context.*
import discord4j.common.util.*
import io.facet.discord.commands.*
import io.facet.discord.commands.arguments.*

/**
 * Get the name of the command alias used.
 */
public val CommandContext<ChatCommandSource>.aliasUsed: String
    get() = rootNode.name

public inline fun <reified T> CommandContext<ChatCommandSource>.get(name: String): T = getArgument(name, T::class.java)

public fun CommandContext<ChatCommandSource>.getInt(name: String): Int = get(name)

public fun CommandContext<ChatCommandSource>.getBool(name: String): Boolean = get(name)

public fun CommandContext<ChatCommandSource>.getDouble(name: String): Double = get(name)

public fun CommandContext<ChatCommandSource>.getFloat(name: String): Float = get(name)

public fun CommandContext<ChatCommandSource>.getLong(name: String): Long = get(name)

public fun CommandContext<ChatCommandSource>.getString(name: String): String = get(name)

public fun CommandContext<ChatCommandSource>.getSnowflake(name: String): Snowflake = get(name)

public fun CommandContext<ChatCommandSource>.getMembers(name: String): MemberSelector = get(name)

public fun CommandContext<ChatCommandSource>.getRoles(name: String): RoleSelector = get(name)

public fun CommandContext<ChatCommandSource>.getChannels(name: String): TextChannelSelector = get(name)
