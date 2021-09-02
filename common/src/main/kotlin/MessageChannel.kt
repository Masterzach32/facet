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

package io.facet.common

import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import io.facet.common.dsl.EmbedBuilder
import io.facet.common.dsl.MessageBuilder
import io.facet.common.dsl.embed
import io.facet.common.dsl.message

/**
 * Builds a message spec with only content and sends it in the specified channel.
 */
public suspend fun MessageChannel.sendMessage(content: String): Message = createMessage(content).await()

/**
 * Builds a [MessageCreateSpec] and sends it in the specified channel.
 */
public suspend fun MessageChannel.sendMessage(
    block: MessageBuilder.() -> Unit
): Message = createMessage(message(block)).await()

/**
 * Sends a message in the specified channel based on the [MessageCreateSpec].
 */
public suspend fun MessageChannel.sendMessage(
    template: MessageCreateSpec
): Message = createMessage(template).await()

/**
 * Builds an [EmbedCreateSpec] and sends it in the specified channel.
 */
public suspend fun MessageChannel.sendEmbed(
    block: EmbedBuilder.() -> Unit
): Message = createMessage(embed(block)).await()

/**
 * Sends a message with an embed in the specified channel based on the [EmbedCreateSpec].
 */
public suspend fun MessageChannel.sendMessage(
    vararg specs: EmbedCreateSpec
): Message = createMessage(*specs).await()

/**
 * Sends an embed in the specified channel based on the [EmbedCreateSpec].
 */
@Deprecated("Use sendMessage", ReplaceWith("sendMessage()"))
public suspend fun MessageChannel.sendEmbed(
    spec: EmbedCreateSpec
): Message = createMessage(spec).await()
