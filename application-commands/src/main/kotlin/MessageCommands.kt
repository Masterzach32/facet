package io.facet.commands

public sealed interface MessageCommand<in C : MessageCommandContext> : ApplicationCommand<C>

/**
 * An application command that is available globally.
 */
public interface GlobalMessageCommand : MessageCommand<GlobalMessageCommandContext>, GlobalApplicationCommand<GlobalMessageCommandContext>

/**
 * An application command that is available globally, but can only be used from within a guild.
 */
public interface GlobalGuildMessageCommand : MessageCommand<GuildMessageCommandContext>, GlobalGuildApplicationCommand<GuildMessageCommandContext>

/**
 * An application command that is only available within a specific guild.
 */
public interface GuildMessageCommand : MessageCommand<GuildMessageCommandContext>, GuildApplicationCommand<GuildMessageCommandContext>
