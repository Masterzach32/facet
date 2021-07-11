package io.facet.discord.appcommands

import discord4j.common.annotations.*
import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.command.*
import discord4j.core.`object`.entity.*
import discord4j.core.event.domain.interaction.*

/*
 * facet - Created on 6/5/2021
 * Author: Zach Kozar
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at the project root.
 */

/**
 * The context for an interaction with an application command.
 *
 * @author Zach Kozar
 * @version 6/5/2021
 */
public abstract class SlashCommandContext(
    /**
     * The discord interaction event.
     */
    public val event: SlashCommandEvent
) {

    public val client: GatewayDiscordClient = event.client
    public val interaction: Interaction = event.interaction
    public val channelId: Snowflake = interaction.channelId
    public val user: User = interaction.user

    @Experimental
    public val options: InteractionOptions = InteractionOptions(interaction.commandInteraction.get())
}
