package io.facet.discord.appcommands

import discord4j.common.util.*
import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import discord4j.core.event.domain.*
import io.facet.discord.extensions.*
import kotlinx.coroutines.*

/*
 * facet - Created on 6/13/2021
 * Author: Zach Kozar
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at the project root.
 */

/**
 * @author Zach Kozar
 * @version 6/13/2021
 */
class GuildInteractionContext(
    event: InteractionCreateEvent,
    workerScope: CoroutineScope
) : InteractionContext(event, workerScope) {

    val guildId: Snowflake = interaction.guildId.get()
    val member: Member = interaction.member.get()

    suspend fun getGuild(): Guild = interaction.guild.await()
    suspend fun getChannel(): GuildMessageChannel = interaction.channel.await() as GuildMessageChannel
}
