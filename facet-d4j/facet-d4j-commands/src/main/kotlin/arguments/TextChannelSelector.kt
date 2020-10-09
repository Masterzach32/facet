package io.facet.discord.commands.arguments

import com.mojang.brigadier.*
import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.entity.channel.*
import io.facet.discord.extensions.*

class TextChannelSelector : EntitySelector<GuildMessageChannel>() {

    override fun parse(reader: StringReader) {
        TODO("Not yet implemented")
    }

    override suspend fun get(client: GatewayDiscordClient, guildId: Snowflake): GuildMessageChannel = entities
        .first()
        .let { client.getChannelById(it).await() as GuildMessageChannel }

    override suspend fun getMultiple(client: GatewayDiscordClient, guildId: Snowflake): List<GuildMessageChannel> = entities
        .map { client.getChannelById(it).await() as GuildMessageChannel }
}
