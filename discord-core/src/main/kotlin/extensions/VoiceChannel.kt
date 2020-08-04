package io.facet.discord.extensions

import discord4j.common.util.*
import discord4j.core.`object`.*
import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import reactor.core.publisher.*

/**
 * Gets the members currently connected to this voice channel by requesting the [VoiceState]s of this guild
 * and filtering by this [VoiceChannel]s Snowflake ID.
 */
@Deprecated(message = "Use coroutine-based functions", replaceWith = ReplaceWith("getConnectedMembers()"))
val VoiceChannel.connectedMembers: Flux<Member>
    get() = voiceStates
        .flatMap(VoiceState::getMember)

/**
 * Gets the members currently connected to this voice channel by requesting the [VoiceState]s of this channel.
 */
suspend fun VoiceChannel.getConnectedMembers(): List<Member> = voiceStates
    .flatMap(VoiceState::getMember)
    .await()

/**
 * Gets the [Snowflake] ids of the members currently connected to this voice channel by requesting the [VoiceState]s
 * of this channel.
 */
suspend fun VoiceChannel.getConnectedMemberIds(): List<Snowflake> = voiceStates
    .await()
    .map(VoiceState::getUserId)
