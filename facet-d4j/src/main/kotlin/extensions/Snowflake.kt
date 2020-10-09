package io.facet.discord.extensions

import discord4j.common.util.*

/**
 * Extension function to turn a [Long] into a [Snowflake]
 */
fun Long.toSnowflake(): Snowflake = Snowflake.of(this)

/**
 * Extension function to turn [String] into a [Snowflake]
 */
fun String.toSnowflake(): Snowflake = Snowflake.of(this)
