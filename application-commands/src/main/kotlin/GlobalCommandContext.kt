package io.facet.commands

import discord4j.common.util.Snowflake
import discord4j.core.`object`.command.Interaction
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.entity.channel.MessageChannel
import io.facet.common.await
import io.facet.common.awaitNullable
import io.facet.common.unwrap

public interface GlobalCommandContext {

    /**
     * The ID of the [server][Guild] that this command was used in. Is null if the command was used in a DM.
     */
    public val guildId: Snowflake?

    /**
     * The [user][User] as a [Member] that invoked this command. Is null if the command was used in a DM.
     */
    public val member: Member?

    /**
     * The server that this command was used in. Is null if the command was used in a DM.
     */
    public suspend fun getGuild(): Guild?

    /**
     * The channel that this command was used in.
     */
    public suspend fun getChannel(): MessageChannel
}

internal class GlobalCommandContextImpl(private val interaction: Interaction) : GlobalCommandContext {

    override val guildId: Snowflake? = interaction.guildId.unwrap()

    override val member: Member? = interaction.member.unwrap()

    override suspend fun getGuild(): Guild? = interaction.guild.awaitNullable()

    override suspend fun getChannel(): MessageChannel = interaction.channel.await()
}
