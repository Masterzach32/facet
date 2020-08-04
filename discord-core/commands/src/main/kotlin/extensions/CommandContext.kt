package io.facet.discord.commands.extensions

import com.mojang.brigadier.context.*
import io.facet.discord.commands.*
import io.facet.discord.commands.arguments.*

/**
 * Get the name of the command alias used.
 */
val CommandContext<ChatCommandSource>.aliasUsed: String
    get() = rootNode.name

inline fun <reified T> CommandContext<ChatCommandSource>.get(name: String): T = getArgument(name, T::class.java)

fun CommandContext<ChatCommandSource>.getInt(name: String): Int = get(name)

fun CommandContext<ChatCommandSource>.getBool(name: String): Boolean = get(name)

fun CommandContext<ChatCommandSource>.getDouble(name: String): Double = get(name)

fun CommandContext<ChatCommandSource>.getFloat(name: String): Float = get(name)

fun CommandContext<ChatCommandSource>.getLong(name: String): Long = get(name)

fun CommandContext<ChatCommandSource>.getString(name: String): String = get(name)

fun CommandContext<ChatCommandSource>.getMembers(name: String): MemberSelector = get(name)

fun CommandContext<ChatCommandSource>.getRoles(name: String): RoleSelector = get(name)

fun CommandContext<ChatCommandSource>.getChannels(name: String): TextChannelSelector = get(name)
