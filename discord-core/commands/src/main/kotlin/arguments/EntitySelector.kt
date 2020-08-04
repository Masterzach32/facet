package io.facet.discord.commands.arguments

import com.mojang.brigadier.*
import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.entity.*

abstract class EntitySelector<E : Entity> {

    protected val entities = mutableListOf<Snowflake>()

    internal abstract fun parse(client: GatewayDiscordClient, reader: StringReader)

    abstract suspend fun get(client: GatewayDiscordClient, guildId: Snowflake): E

    abstract suspend fun getMultiple(client: GatewayDiscordClient, guildId: Snowflake): List<E>
}
