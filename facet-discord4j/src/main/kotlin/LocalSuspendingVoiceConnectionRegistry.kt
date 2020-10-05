package io.facet.discord

import discord4j.common.util.*
import discord4j.voice.*
import io.facet.discord.extensions.*

class LocalSuspendingVoiceConnectionRegistry(
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
