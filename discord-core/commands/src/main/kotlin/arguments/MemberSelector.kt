package io.facet.discord.commands.arguments

import com.mojang.brigadier.*
import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.entity.*
import io.facet.discord.extensions.*

class MemberSelector(val selectMultiple: Boolean, val allowRoles: Boolean) : EntitySelector<Member>() {

    override fun parse(client: GatewayDiscordClient, reader: StringReader) {
        val start = reader.cursor
        val mention = reader.readStringUntil('>')

    }

    override suspend fun get(client: GatewayDiscordClient, guildId: Snowflake): Member = entities
        .first()
        .let { client.getMemberById(guildId, it).await() }

    override suspend fun getMultiple(client: GatewayDiscordClient, guildId: Snowflake): List<Member> = entities
        .map { client.getMemberById(guildId, it).await() }

    companion object {
        val mentionStart = "<@!"
    }
}
