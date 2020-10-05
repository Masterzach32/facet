package io.facet.discord.commands.arguments

import discord4j.core.*

fun snowflake() = SnowflakeArgument

fun member(client: GatewayDiscordClient) = GuildEntityArgument(MemberSelector(selectMultiple = false, allowRoles = false))

fun members(client: GatewayDiscordClient) = GuildEntityArgument(MemberSelector(selectMultiple = true, allowRoles = true))

fun membersStrict(client: GatewayDiscordClient) = GuildEntityArgument(MemberSelector(selectMultiple = true, allowRoles = false))

fun role(client: GatewayDiscordClient) = GuildEntityArgument(RoleSelector(false))

fun roles(client: GatewayDiscordClient) = GuildEntityArgument(RoleSelector(true))

fun textChannel(client: GatewayDiscordClient) = GuildEntityArgument(TextChannelSelector())
