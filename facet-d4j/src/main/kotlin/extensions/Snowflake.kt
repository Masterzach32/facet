package io.facet.discord.extensions

import discord4j.common.util.*
import discord4j.discordjson.*

/**
 * Extension function to turn a [Long] into a [Snowflake]
 */
fun Long.toSnowflake(): Snowflake = Snowflake.of(this)

/**
 * Extension function to turn [String] into a [Snowflake]
 */
fun String.toSnowflake(): Snowflake = Snowflake.of(this)

fun Id.toSnowflake(): Snowflake = asLong().toSnowflake()

fun Snowflake.asULong(): ULong = asLong().toULong()
