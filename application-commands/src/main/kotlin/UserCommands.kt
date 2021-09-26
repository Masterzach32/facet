package io.facet.commands

public sealed interface UserCommand<in C : UserCommandContext> : ApplicationCommand<C>

/**
 * An application command that is available globally.
 */
public interface GlobalUserCommand : UserCommand<GlobalUserCommandContext>, GlobalApplicationCommand<GlobalUserCommandContext>

/**
 * An application command that is available globally, but can only be used from within a guild.
 */
public interface GlobalGuildUserCommand : UserCommand<GuildUserCommandContext>, GlobalGuildApplicationCommand<GuildUserCommandContext>

/**
 * An application command that is only available within a specific guild.
 */
public interface GuildUserCommand : UserCommand<GuildUserCommandContext>, GuildApplicationCommand<GuildUserCommandContext>
