package io.facet.discord.extensions

import discord4j.core.`object`.*
import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*

suspend fun Guild.getOurVoiceState(): VoiceState? = voiceStates.asFlow()
    .firstOrNull { it.userId == client.selfId }

suspend fun Guild.getConnectedVoiceChannel(): VoiceChannel? = getOurVoiceState()?.channel?.await()

suspend fun Guild.getVoiceChannelByName(name: String): VoiceChannel? = channels.asFlow()
    .firstOrNull { it.name == name } as? VoiceChannel
