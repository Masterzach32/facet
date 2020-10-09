package io.facet.discord.commands.arguments

import com.mojang.brigadier.*
import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.entity.*
import io.facet.discord.extensions.*

class RoleSelector(val selectMultiple: Boolean) : EntitySelector<Role>() {

    override fun parse(reader: StringReader) {
        TODO("Not yet implemented")
    }

    override suspend fun get(client: GatewayDiscordClient, guildId: Snowflake): Role = entities
        .first()
        .let { client.getRoleById(guildId, it).await() }

    override suspend fun getMultiple(client: GatewayDiscordClient, guildId: Snowflake): List<Role> = entities
        .map { client.getRoleById(guildId, it).await() }
}
