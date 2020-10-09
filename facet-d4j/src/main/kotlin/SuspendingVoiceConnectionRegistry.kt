package io.facet.discord

import discord4j.common.util.*
import discord4j.voice.*

interface SuspendingVoiceConnectionRegistry {

    suspend fun getVoiceConnection(
        guildId: Snowflake
    ): VoiceConnection

    suspend fun registerVoiceConnection(
        guildId: Snowflake,
        voiceConnection: VoiceConnection
    )

    suspend fun disconnect(
        guildId: Snowflake
    )
}
