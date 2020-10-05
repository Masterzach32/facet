package io.facet.discord.commands.events

import discord4j.core.*
import discord4j.gateway.*
import io.facet.discord.commands.*

class CommandExecutedEvent(
    client: GatewayDiscordClient,
    shardInfo: ShardInfo,
    command: ChatCommand,
    source: ChatCommandSource,
    val aliasUsed: String
) : CommandEvent(client, shardInfo, command, source)
