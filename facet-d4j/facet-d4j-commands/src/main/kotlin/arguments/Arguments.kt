package io.facet.discord.commands.arguments

import com.mojang.brigadier.arguments.*
import discord4j.common.util.*

public fun snowflake(): ArgumentType<Snowflake> = SnowflakeArgument

public fun member(): ArgumentType<MemberSelector> = GuildEntityArgument(MemberSelector(selectMultiple = false, allowRoles = false))

public fun members(): ArgumentType<MemberSelector> = GuildEntityArgument(MemberSelector(selectMultiple = true, allowRoles = true))

public fun membersStrict(): ArgumentType<MemberSelector> = GuildEntityArgument(MemberSelector(selectMultiple = true, allowRoles = false))

public fun role(): ArgumentType<RoleSelector> = GuildEntityArgument(RoleSelector(false))

public fun roles(): ArgumentType<RoleSelector> = GuildEntityArgument(RoleSelector(true))

public fun textChannel(): ArgumentType<TextChannelSelector> = GuildEntityArgument(TextChannelSelector())
