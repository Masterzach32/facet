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

package io.facet.discord

import discord4j.common.util.*
import discord4j.voice.*
import io.facet.discord.extensions.*

public class LocalSuspendingVoiceConnectionRegistry(
    private val registry: VoiceConnectionRegistry = LocalVoiceConnectionRegistry()
) : SuspendingVoiceConnectionRegistry {

    override suspend fun getVoiceConnection(
        guildId: Snowflake
    ): VoiceConnection = registry.getVoiceConnection(guildId).await()

    override suspend fun registerVoiceConnection(
        guildId: Snowflake,
        voiceConnection: VoiceConnection
    ): Unit = registry.registerVoiceConnection(guildId, voiceConnection).await()

    override suspend fun disconnect(
        guildId: Snowflake
    ): Unit = registry.disconnect(guildId).await()
}
