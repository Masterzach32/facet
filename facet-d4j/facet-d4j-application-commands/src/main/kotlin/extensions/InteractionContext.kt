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

package io.facet.discord.appcommands.extensions

import discord4j.core.spec.*
import discord4j.discordjson.json.*
import io.facet.discord.appcommands.*
import io.facet.discord.dsl.*
import io.facet.discord.extensions.*

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

public suspend fun SlashCommandContext.acknowledge(): Unit = event.acknowledge().await()

public suspend fun SlashCommandContext.acknowledgeEphemeral(): Unit = event.acknowledgeEphemeral().await()

@Deprecated(
    "Use functions on interactionResponse", ReplaceWith(
        "event.interactionResponse.createFollowupMessage(content).await()",
        "io.facet.discord.extensions.await"
    )
)
public suspend fun SlashCommandContext.createFollowupMessage(content: String): MessageData = event
    .interactionResponse
    .createFollowupMessage(content)
    .await()

@Deprecated(
    "", ReplaceWith(
        "event.interactionResponse.createFollowupMessage(template.asMultipartRequest()).await()",
        "io.facet.discord.extensions.await"
    )
)
public suspend fun SlashCommandContext.createFollowupMessage(template: EmbedCreateSpec): MessageData {
    return event.interactionResponse.createFollowupMessage(template.asMultipartRequest()).await()
}

@Deprecated(
    "", ReplaceWith(
        "event.interactionResponse.createFollowupMessage(embed(buildBlock).asMultipartRequest()).await()",
        "io.facet.discord.dsl.embed",
        "io.facet.discord.extensions.await"
    )
)
public suspend fun SlashCommandContext.createFollowupMessage(buildBlock: EmbedBuilder.() -> Unit): MessageData {
    return event.interactionResponse.createFollowupMessage(embed(buildBlock).asMultipartRequest()).await()
}
