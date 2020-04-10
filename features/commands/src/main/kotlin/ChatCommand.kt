package io.discordcommons.commands

import com.mojang.brigadier.*
import discord4j.core.event.domain.message.*

interface ChatCommand {

    fun register(dispatcher: CommandDispatcher<MessageCreateEvent>)
}
