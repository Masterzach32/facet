package io.facet.discord.extensions

import discord4j.core.`object`.*
import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*

/**
 * Gets the [VoiceState] of our user in this [Guild].
 */
suspend fun Guild.getOurVoiceState(): VoiceState? = voiceStates.asFlow()
    .firstOrNull { it.userId == client.selfId }

/**
 * Gets the [VoiceChannel] that our user is connected to in this [Guild].
 */
suspend fun Guild.getConnectedVoiceChannel(): VoiceChannel? = getOurVoiceState()?.channel?.await()

/**
 * Returns the first [VoiceChannel] found with the specified name.
 */
suspend fun Guild.getVoiceChannelByName(name: String): VoiceChannel? = channels.asFlow()
    .firstOrNull { it.name == name } as? VoiceChannel
