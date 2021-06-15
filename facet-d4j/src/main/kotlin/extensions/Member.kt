package io.facet.discord.extensions

import discord4j.core.`object`.*
import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*

/**
 * Gets the [VoiceChannel] that the member is currently connected to, if present.
 */
suspend fun Member.getConnectedVoiceChannel(): VoiceChannel? = voiceState
    .flatMap(VoiceState::getChannel)
    .awaitNullable()