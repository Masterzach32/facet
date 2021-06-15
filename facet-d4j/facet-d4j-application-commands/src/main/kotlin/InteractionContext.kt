package io.facet.discord.appcommands

import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.command.*
import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import discord4j.core.event.domain.*
import io.facet.core.extensions.*
import io.facet.discord.extensions.*
import kotlinx.coroutines.*

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
abstract class InteractionContext(
    /**
     * The discord interaction event.
     */
    val event: InteractionCreateEvent,
    /**
     * The [CoroutineScope] of the command worker coroutine that is processing this command.
     */
    @Deprecated("If you need access to a CoroutineScope, use the coroutineScope {} builder.")
    val workerScope: CoroutineScope
) {

    val client: GatewayDiscordClient = event.client
    val interaction: Interaction = event.interaction
    val user: User = interaction.user

    val command: ApplicationCommandInteraction = interaction.commandInteraction
}
