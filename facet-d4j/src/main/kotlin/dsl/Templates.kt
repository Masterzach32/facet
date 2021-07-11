package io.facet.discord.dsl

import discord4j.core.spec.*

public interface SpecBuilder<out S : Spec<*>> {

    public fun build(): S
}
