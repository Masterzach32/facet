package io.facet.discord.commands.events

import discord4j.core.*
import discord4j.core.event.domain.*
import discord4j.gateway.*
import io.facet.discord.commands.*

abstract class CommandEvent(
    client: GatewayDiscordClient,
    shardInfo: ShardInfo,
    val command: ChatCommand,
    val source: ChatCommandSource
) : Event(client, shardInfo)