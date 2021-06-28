package io.facet.discord.appcommands

import discord4j.discordjson.json.*

/*
 * facet - Created on 6/5/2021
 * Author: Zach Kozar
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at the project root.
 */

/**
 * A discord application "slash" command.
 *
 * @author Zach Kozar
 * @version 6/5/2021
 */
sealed interface ApplicationCommand<in C : SlashCommandContext> {

    /**
     * The discord-json request body to be sent to the Discord API
     */
    val request: ApplicationCommandRequest

    /**
     * Called when this command is used in an interaction
     */
    suspend fun C.execute()
}
