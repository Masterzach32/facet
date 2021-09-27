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

import discord4j.common.util.Snowflake
import discord4j.discordjson.json.ApplicationCommandRequest

/**
 * A discord application command.
 */
public sealed interface ApplicationCommand<in C : ApplicationCommandContext> {

    /**
     * The discord-json request body to be sent to the Discord API
     */
    public val request: ApplicationCommandRequest

    /**
     * Called when this command is used in an interaction
     */
    public suspend fun C.execute()
}

public sealed interface GlobalApplicationCommand<in C> : ApplicationCommand<C>
    where C : ApplicationCommandContext, C : GlobalCommandContext

public sealed interface GlobalGuildApplicationCommand<in C> : ApplicationCommand<C>
    where C : ApplicationCommandContext, C : GuildCommandContext

public sealed interface GuildApplicationCommand<in C> : ApplicationCommand<C>
    where C : ApplicationCommandContext, C : GuildCommandContext {

    /**
     * The [Snowflake] id of the guild that this command is available in.
     */
    public val guildId: Snowflake
}
