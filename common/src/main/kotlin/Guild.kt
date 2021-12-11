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

import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.channel.AudioChannel
import discord4j.core.`object`.entity.channel.VoiceChannel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.reactive.asFlow

/**
 * Gets the [VoiceState] of our user in this [Guild].
 */
public suspend fun Guild.getOurVoiceState(): VoiceState? = voiceStates.asFlow()
    .firstOrNull { it.userId == client.selfId }

/**
 * Gets the [VoiceChannel] that our user is connected to in this [Guild].
 */
public suspend fun Guild.getConnectedVoiceChannel(): AudioChannel? = getOurVoiceState()?.channel?.await()

/**
 * Returns the first [VoiceChannel] found with the specified name.
 */
public suspend fun Guild.getVoiceChannelByName(name: String): AudioChannel? = channels.asFlow()
    .firstOrNull { it.name == name } as? VoiceChannel
