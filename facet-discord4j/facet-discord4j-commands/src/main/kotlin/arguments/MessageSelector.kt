package io.facet.discord.commands.arguments

import com.mojang.brigadier.*
import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.entity.*

class MessageSelector : EntitySelector<Member>() {

    override fun parse(reader: StringReader) {
        TODO("Not yet implemented")
    }

    override suspend fun get(client: GatewayDiscordClient, guildId: Snowflake): Member {
        TODO("Not yet implemented")
    }

    override suspend fun getMultiple(client: GatewayDiscordClient, guildId: Snowflake): List<Member> {
        TODO("Not yet implemented")
    }
}
