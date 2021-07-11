package io.facet.discord.appcommands

/*
 * facet - Created on 6/13/2021
 * Author: Zach Kozar
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at the project root.
 */

/**
 * An application command that is available globally, but can only be used from within a guild.
 *
 * @author Zach Kozar
 * @version 6/13/2021
 */
public interface GlobalGuildApplicationCommand : ApplicationCommand<GuildSlashCommandContext>
