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

package io.facet.commands

import discord4j.common.annotations.*
import discord4j.common.util.*
import discord4j.core.*
import discord4j.core.`object`.command.*
import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import discord4j.core.event.domain.interaction.*
import io.facet.common.*
import kotlinx.coroutines.*

/**
 * The context for an interaction with an application command.
 */
public abstract class SlashCommandContext(
    /**
     * The discord interaction event.
     */
    public val event: SlashCommandEvent,
    scope: CoroutineScope
) : CoroutineScope by scope {

    /**
     * The gateway this event was dispatched from.
     */
    public val client: GatewayDiscordClient = event.client

    /**
     * The discord interaction for this command context.
     */
    public val interaction: Interaction = event.interaction

    /**
     * The ID of the [channel][MessageChannel] where this command was used.
     */
    public val channelId: Snowflake = interaction.channelId

    /**
     * The user that invoked this command.
     */
    public val user: User = interaction.user

    /**
     * The interaction followup handler.
     */
    public val interactionResponse: GatewayInteractionResponse = event.gatewayInteractionResponse

    /**
     * Experimental class for getting application command options through delegation.
     *
     * Example:
     *
     * ```kotlin
     * val name: String by options
     * val count: Int by options
     * val enabled: Boolean by options
     * val user: Mono<User> by options
     * val channel: Mono<Channel> by options
     * val role: Mono<Role> by options
     *
     * // also
     * val nullableName: String? by options.nullable()
     * val defaultName: String by options.defaultValue("test")
     * ```
     */
    @Experimental
    public val options: InteractionOptions = InteractionOptions(interaction.commandInteraction.get())
}

/**
 * Acknowledges the interaction indicating a response will be edited later. The user sees a loading state,
 * visible to all participants in the invoking channel. For a "only you can see this" response, set [ephemeral] to
 * `true`, or use acknowledgeEphemeral().
 */
public suspend fun SlashCommandContext.acknowledge(ephemeral: Boolean = false): Unit =
    if (ephemeral)
        event.acknowledgeEphemeral().await()
    else
        event.acknowledge().await()

/**
 * Acknowledges the interaction indicating a response will be edited later. Only the invoking user sees a loading state.
 */
public suspend fun SlashCommandContext.acknowledgeEphemeral(): Unit = event.acknowledgeEphemeral().await()
