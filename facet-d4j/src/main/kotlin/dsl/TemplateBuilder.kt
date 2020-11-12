package io.facet.discord.dsl

interface TemplateBuilder<Spec, out Template>{

    val spec: Spec

    fun toTemplate(): Template
}
