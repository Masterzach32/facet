package io.facet.discord.extensions

import discord4j.common.util.*
import discord4j.core.`object`.*
import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*

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
