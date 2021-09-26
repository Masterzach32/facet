package io.facet.commands

import discord4j.common.util.Snowflake
import discord4j.core.`object`.command.Interaction
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.entity.channel.GuildMessageChannel
import io.facet.common.await

public interface GuildCommandContext {

    /**
     * The ID of the [server][Guild] that this command was used in.
     */
    public val guildId: Snowflake

    /**
     * The [user][User] as a [Member] that invoked this command.
     */
    public val member: Member

    /**
     * The server that this command was used in.
     */
    public suspend fun getGuild(): Guild

    /**
     * The channel that this command was used in.
     */
    public suspend fun getChannel(): GuildMessageChannel
}

internal class GuildCommandContextImpl(private val interaction: Interaction) : GuildCommandContext {

    override val guildId: Snowflake = interaction.guildId.get()

    override val member: Member = interaction.member.get()

    override suspend fun getGuild(): Guild = interaction.guild.await()

    override suspend fun getChannel(): GuildMessageChannel = interaction.channel.await() as GuildMessageChannel
}
