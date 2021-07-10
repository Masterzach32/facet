package io.facet.discord.commands

import com.mojang.brigadier.*
import discord4j.rest.util.*
import io.facet.discord.commands.dsl.*
import io.facet.discord.commands.extensions.*

abstract class ChatCommand(
    val name: String,
    val aliases: Set<String>,
    val scope: Scope = Scope.ALL,
    val category: String = "none",
    val description: String? = null,
    val discordPermsRequired: PermissionSet = PermissionSet.none(),
    val usage: CommandUsage? = null
) {

    protected abstract fun DSLCommandNode<ChatCommandSource>.register()

    internal fun register(dispatcher: CommandDispatcher<ChatCommandSource>) {
        require(aliases.isNotEmpty()) { "Command $name must have at least one alias!" }

        dispatcher.literal(aliases.first()) {
            aliases.drop(1).forEach { alias(it) }

            register()
        }
    }
}