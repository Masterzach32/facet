package io.discordcommons.core.extensions

import discord4j.core.`object`.util.*

fun Long.toSnowflake() = Snowflake.of(this)
