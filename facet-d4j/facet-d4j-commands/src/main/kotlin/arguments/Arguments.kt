package io.facet.discord.commands.arguments

fun snowflake() = SnowflakeArgument

fun member() = GuildEntityArgument(MemberSelector(selectMultiple = false, allowRoles = false))

fun members() = GuildEntityArgument(MemberSelector(selectMultiple = true, allowRoles = true))

fun membersStrict() = GuildEntityArgument(MemberSelector(selectMultiple = true, allowRoles = false))

fun role() = GuildEntityArgument(RoleSelector(false))

fun roles() = GuildEntityArgument(RoleSelector(true))

fun textChannel() = GuildEntityArgument(TextChannelSelector())
