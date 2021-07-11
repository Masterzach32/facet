package io.facet.discord.commands

import com.mojang.brigadier.*
import discord4j.rest.util.*
import io.facet.discord.commands.dsl.*
import io.facet.discord.commands.extensions.*

public abstract class ChatCommand(
    public val name: String,
    public val aliases: Set<String>,
    public val scope: Scope = Scope.ALL,
    public val category: String = "none",
    public val description: String? = null,
    public val discordPermsRequired: PermissionSet = PermissionSet.none(),
    public val usage: CommandUsage? = null
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
