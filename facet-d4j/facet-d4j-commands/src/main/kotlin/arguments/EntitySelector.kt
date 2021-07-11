package io.facet.discord.commands.arguments

import com.mojang.brigadier.*
import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.entity.*

public abstract class EntitySelector<E : Entity> {

    protected val entities: MutableList<Snowflake> = mutableListOf()

    internal abstract fun parse(reader: StringReader)

    public abstract suspend fun get(client: GatewayDiscordClient, guildId: Snowflake): E

    public abstract suspend fun getMultiple(client: GatewayDiscordClient, guildId: Snowflake): List<E>
}
