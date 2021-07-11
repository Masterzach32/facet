package io.facet.discord.exposed

import discord4j.common.util.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*

public open class SnowflakeIdTable(name: String = "", columnName: String = "id") : IdTable<Snowflake>(name) {

    override val id: Column<EntityID<Snowflake>> = snowflake(columnName).entityId()

    override val primaryKey: PrimaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
}
