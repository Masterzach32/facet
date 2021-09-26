package io.facet.commands

public sealed interface SlashCommand<in C : SlashCommandContext> : ApplicationCommand<C>

/**
 * An application command that is available globally.
 */
public interface GlobalSlashCommand : SlashCommand<GlobalSlashCommandContext>, GlobalApplicationCommand<GlobalSlashCommandContext>

/**
 * An application command that is available globally, but can only be used from within a guild.
 */
public interface GlobalGuildSlashCommand : SlashCommand<GuildSlashCommandContext>, GlobalGuildApplicationCommand<GuildSlashCommandContext>

/**
 * An application command that is only available within a specific guild.
 */
public interface GuildSlashCommand : SlashCommand<GuildSlashCommandContext>, GuildApplicationCommand<GuildSlashCommandContext>