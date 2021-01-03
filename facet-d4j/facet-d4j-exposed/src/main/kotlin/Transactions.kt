package io.facet.discord.exposed

import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import kotlin.coroutines.*

fun Transaction.create(vararg tables: Table) {
    SchemaUtils.create(*tables)
}

suspend fun <T> sql(
    context: CoroutineContext = Dispatchers.IO,
    sqlcode: Transaction.() -> T
): T = withContext(context) {
    transaction(statement = sqlcode)
}