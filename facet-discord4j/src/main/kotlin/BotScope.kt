package io.facet.discord

import kotlinx.coroutines.*

/**
 * The bot's coroutine scope, used as the root coroutine scope for event listeners.
 */
object BotScope : CoroutineScope by CoroutineScope(SupervisorJob())
