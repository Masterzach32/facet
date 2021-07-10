package io.facet.discord.dsl

import discord4j.core.spec.*

interface SpecBuilder<out S : Spec<*>> {

    fun build(): S
}
