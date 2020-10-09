package io.facet.discord.exposed.dao

import discord4j.common.util.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.dao.id.EntityID

abstract class SnowflakeEntity(id: EntityID<Snowflake>) : Entity<Snowflake>(id)

abstract class SnowflakeEntityClass<out E : SnowflakeEntity>(
    table: IdTable<Snowflake>,
    entityType: Class<E>? = null
) : EntityClass<Snowflake, E>(table, entityType)
