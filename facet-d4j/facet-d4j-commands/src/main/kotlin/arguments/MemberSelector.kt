package io.facet.discord.commands.arguments

import com.mojang.brigadier.*
import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.entity.*
import io.facet.discord.extensions.*

public class MemberSelector(private val selectMultiple: Boolean, private val allowRoles: Boolean) : EntitySelector<Member>() {

    override fun parse(reader: StringReader) {
        TODO("Not yet implemented")
    }

    override suspend fun get(client: GatewayDiscordClient, guildId: Snowflake): Member = entities
        .first()
        .let { client.getMemberById(guildId, it).await() }

    override suspend fun getMultiple(client: GatewayDiscordClient, guildId: Snowflake): List<Member> = entities
        .map { client.getMemberById(guildId, it).await() }

    private companion object {
        val mentionStart = "<@!"
    }
}
