package io.facet.discord

import discord4j.common.util.*
import discord4j.voice.*

public interface SuspendingVoiceConnectionRegistry {

    public suspend fun getVoiceConnection(
        guildId: Snowflake
    ): VoiceConnection

    public suspend fun registerVoiceConnection(
        guildId: Snowflake,
        voiceConnection: VoiceConnection
    )

    public suspend fun disconnect(
        guildId: Snowflake
    )
}
