package io.facet.discord.extensions

import discord4j.core.`object`.*
import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*
import reactor.core.publisher.*

@Deprecated(message = "Use coroutine-based functions", replaceWith = ReplaceWith("getOurVoiceState()"))
val Guild.ourVoiceState: Mono<VoiceState>
    get() = voiceStates.filter { vs -> vs.userId == client.selfId }.toMono()

@Deprecated(message = "Use coroutine-based functions", replaceWith = ReplaceWith("getOurConnectedVoiceChannel()"))
val Guild.ourConnectedVoiceChannel: Mono<VoiceChannel>
    get() = ourVoiceState.flatMap { it.channel }

suspend fun Guild.getOurVoiceState(): VoiceState = ourVoiceState.await()

suspend fun Guild.getOurConnectedVoiceChannel(): VoiceChannel? = ourConnectedVoiceChannel.awaitNullable()

suspend fun Guild.getVoiceChannelByName(name: String): VoiceChannel? = channels.asFlow()
    .firstOrNull { it.name == name } as? VoiceChannel
