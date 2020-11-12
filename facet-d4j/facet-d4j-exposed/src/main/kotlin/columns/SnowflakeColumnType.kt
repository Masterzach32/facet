package io.facet.discord.exposed.columns

import discord4j.common.util.*
import io.facet.discord.extensions.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.vendors.*

class SnowflakeColumnType : ColumnType() {

    override fun sqlType(): String = currentDialect.dataTypeProvider.longType()

    override fun valueFromDB(value: Any): Any = when (value) {
        is Long -> value
        is Number -> value.toLong()
        is String -> value.toLong()
        else -> error("Expected value of type Long, instead found $value of type ${value::class.qualifiedName}")
    }.toSnowflake()

    override fun notNullValueToDB(value: Any): Any = (value as Snowflake).asLong()

    override fun nonNullValueToString(value: Any): String = notNullValueToDB(value).toString()
}

/**
 * Creates a numeric column, with the specified [name], for storing unsigned 64-bit snowflake IDs.
 */
fun Table.snowflake(name: String): Column<Snowflake> = registerColumn(name, SnowflakeColumnType())