/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.facet.discord.appcommands

import discord4j.common.annotations.*
import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.command.*
import discord4j.core.`object`.entity.*
import discord4j.core.event.domain.interaction.*
import io.facet.discord.extensions.*

/**
 * The context for an interaction with an application command.
 */
public abstract class SlashCommandContext(
    /**
     * The discord interaction event.
     */
    public val event: SlashCommandEvent
) {

    /**
     * The gateway this event was dispatched from.
     */
    public val client: GatewayDiscordClient = event.client

    /**
     * The interaction for this context.
     */
    public val interaction: Interaction = event.interaction

    /**
     * The interaction followup handler.
     */
    public val interactionResponse: GatewayInteractionResponse = event.gatewayInteractionResponse

    /**
     * The channel snowflake ID where this interaction occurred.
     */
    public val channelId: Snowflake = interaction.channelId

    /**
     * The user that invoked this interaction.
     */
    public val user: User = interaction.user

    /**
     * Experimental class for getting application command options through delegation.
     */
    @Experimental
    public val options: InteractionOptions = InteractionOptions(interaction.commandInteraction.get())
}
