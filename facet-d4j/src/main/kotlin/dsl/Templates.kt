package io.facet.discord.dsl

import java.util.function.*

interface Template<Spec> : Consumer<Spec>, (Spec) -> Unit

interface TemplateBuilder<Spec> {

    val spec: Spec
}
