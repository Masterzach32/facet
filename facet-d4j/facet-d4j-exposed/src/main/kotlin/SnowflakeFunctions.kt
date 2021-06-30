package io.facet.discord.exposed

import discord4j.common.util.*
import org.jetbrains.exposed.sql.*

fun snowflakeLiteral(snowflake: Snowflake): LiteralOp<Snowflake> = LiteralOp(SnowflakeColumnType.INSTANCE, snowflake)