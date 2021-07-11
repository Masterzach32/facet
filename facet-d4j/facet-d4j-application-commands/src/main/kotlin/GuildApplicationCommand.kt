package io.facet.discord.appcommands

import discord4j.common.util.*

/*
 * facet - Created on 6/13/2021
 * Author: Zach Kozar
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at the project root.
 */

/**
 * An application command that is only available within a specific guild.
 *
 * @author Zach Kozar
 * @version 6/13/2021
 */
public interface GuildApplicationCommand : ApplicationCommand<GuildSlashCommandContext> {

    /**
     * The [Snowflake] id of the guild that this command is available in.
     */
    public val guildId: Snowflake
}
