package io.facet.discord.exposed

import org.jetbrains.exposed.sql.*

fun Transaction.create(vararg tables: Table) {
    SchemaUtils.create(*tables)
}