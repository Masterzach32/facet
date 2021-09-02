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

import discord4j.common.util.Snowflake
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.channel.VoiceChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.reactive.asFlow

/**
 * Gets the members currently connected to this [VoiceChannel] by requesting the [VoiceState]s of this guild
 * and filtering by this channel's Snowflake ID.
 */
public val VoiceChannel.connectedMembers: Flow<Member>
    get() = voiceStates.asFlow()
        .map { it.member.await() }

/**
 * Gets the members currently connected to this voice channel by requesting the [VoiceState]s of this channel.
 */
public suspend fun VoiceChannel.getConnectedMembers(): Set<Member> = connectedMembers.toSet()

/**
 * Gets the [Snowflake] ids of the members currently connected to this voice channel by requesting the [VoiceState]s
 * of this channel.
 */
public suspend fun VoiceChannel.getConnectedMemberIds(): Set<Snowflake> = voiceStates
    .await()
    .map(VoiceState::getUserId)
    .toSet()
