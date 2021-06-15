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
 * @author Zach Kozar
 * @version 6/5/2021
 */
sealed interface ApplicationCommand<in C : ApplicationCommandContext> {

    val request: ApplicationCommandRequest

    suspend fun C.execute()
}
