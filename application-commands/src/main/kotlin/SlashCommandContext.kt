package io.facet.commands

import discord4j.core.`object`.entity.Guild
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import kotlinx.coroutines.CoroutineScope

public sealed class SlashCommandContext(
    public override val event: ChatInputInteractionEvent,
    scope: CoroutineScope
) : ApplicationCommandContext(event, scope) {

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
    @ExperimentalStdlibApi
    public val options: InteractionOptions = InteractionOptions(interaction.commandInteraction.get())
}

/**
 * The context for an interaction with an [ApplicationCommand] that could have been used in a DM.
 */
public class GlobalSlashCommandContext(
    event: ChatInputInteractionEvent,
    scope: CoroutineScope
) : SlashCommandContext(event, scope),
    GlobalCommandContext by GlobalCommandContextImpl(event.interaction)

/**
 * The context for an interaction with an [ApplicationCommand] that occurred in a [server][Guild].
 */
public class GuildSlashCommandContext(
    event: ChatInputInteractionEvent,
    scope: CoroutineScope
) : SlashCommandContext(event, scope),
    GuildCommandContext by GuildCommandContextImpl(event.interaction)
