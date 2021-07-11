package io.facet.discord.extensions

import discord4j.common.util.*
import discord4j.discordjson.*

/**
 * Extension function to turn a [Long] into a [Snowflake]
 */
public fun Long.toSnowflake(): Snowflake = Snowflake.of(this)

/**
 * Extension function to turn [String] into a [Snowflake]
 */
public fun String.toSnowflake(): Snowflake = Snowflake.of(this)

public fun Id.toSnowflake(): Snowflake = asLong().toSnowflake()

public fun Snowflake.asULong(): ULong = asLong().toULong()
