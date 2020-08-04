package io.facet.discord.commands.arguments

import discord4j.core.*

fun member(client: GatewayDiscordClient) = GuildEntityArgument(client, MemberSelector(selectMultiple = false, allowRoles = false))

fun members(client: GatewayDiscordClient) = GuildEntityArgument(client, MemberSelector(selectMultiple = true, allowRoles = true))

fun membersStrict(client: GatewayDiscordClient) = GuildEntityArgument(client, MemberSelector(selectMultiple = true, allowRoles = false))

fun role(client: GatewayDiscordClient) = GuildEntityArgument(client, RoleSelector(false))

fun roles(client: GatewayDiscordClient) = GuildEntityArgument(client, RoleSelector(true))

fun textChannel(client: GatewayDiscordClient) = GuildEntityArgument(client, TextChannelSelector())
