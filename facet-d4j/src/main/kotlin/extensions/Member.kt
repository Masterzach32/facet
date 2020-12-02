package io.facet.discord.extensions

import discord4j.core.`object`.*
import discord4j.core.`object`.entity.*
import discord4j.core.`object`.entity.channel.*

suspend fun Member.getConnectedVoiceChannel(): VoiceChannel? = voiceState
    .flatMap(VoiceState::getChannel)
    .awaitNullable()