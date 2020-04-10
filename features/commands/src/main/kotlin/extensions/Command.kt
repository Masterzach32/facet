package io.discordcommons.commands.extensions

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.*
import discord4j.core.event.domain.message.*
import io.discordcommons.commands.ChatCommand

fun ChatCommand.literal(name: String) = LiteralArgumentBuilder.literal<MessageCreateEvent>(name)

fun <T> ChatCommand.argument(name: String, type: ArgumentType<T>) = RequiredArgumentBuilder.argument<MessageCreateEvent, T>(name, type)
