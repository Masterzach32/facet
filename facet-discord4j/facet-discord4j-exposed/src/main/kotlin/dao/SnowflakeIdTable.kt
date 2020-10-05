package io.facet.discord.exposed.dao

import discord4j.common.util.*
import io.facet.discord.exposed.columns.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*

open class SnowflakeIdTable(name: String = "", columnName: String = "id") : IdTable<Snowflake>(name) {

    override val id: Column<EntityID<Snowflake>> = snowflake(columnName).entityId()

    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
}
