package io.facet.discord.appcommands

import discord4j.core.`object`.entity.*

/*
 * facet - Created on 6/6/2021
 * Author: Zach Kozar
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at the project root.
 */

/**
 * @author Zach Kozar
 * @version 6/6/2021
 */
public interface PermissibleApplicationCommand {

    public suspend fun hasPermission(user: User, guild: Guild?): Boolean
}
