package io.facet.discord.dsl

import discord4j.core.spec.*
import discord4j.rest.util.*
import java.time.*

/**
 * Creates a new [EmbedTemplate] using an [EmbedBuilder].
 */
fun embed(buildBlock: EmbedBuilder.() -> Unit): EmbedTemplate = EmbedTemplate(buildBlock)

class EmbedTemplate(
    private val template: EmbedBuilder.() -> Unit
) : Template<EmbedCreateSpec> {

    override fun accept(spec: EmbedCreateSpec) = EmbedBuilder(spec).run(template)

    override fun invoke(spec: EmbedCreateSpec) = accept(spec)

    fun andThen(buildBlock: EmbedBuilder.() -> Unit): EmbedTemplate = embed {
        template()
        buildBlock()
    }
}

class EmbedBuilder internal constructor(
    override val spec: EmbedCreateSpec
) : TemplateBuilder<EmbedCreateSpec> {

    var title: String = ""
        set(value) = spec.setTitle(value).let { field = value }

    var description: String = ""
        set(value) = spec.setDescription(value).let { field = value }

    var url: String = ""
        set(value) = spec.setUrl(value).let { field = value }

    var timestamp: Instant = Instant.EPOCH
        set(value) = spec.setTimestamp(value).let { field = value }

    var color: Color = Color.WHITE
        set(value) = spec.setColor(value).let { field = value }

    var imageUrl: String = ""
        set(value) = spec.setImage(value).let { field = value }

    var thumbnailUrl: String = ""
        set(value) = spec.setThumbnail(value).let { field = value }

    fun footer(text: String, icon: String? = null) {
        spec.setFooter(text, icon)
    }

    fun footer(footerSpec: EmbedFooter.() -> Unit) {
        EmbedFooter().apply(footerSpec).apply {
            footer(text, iconUrl)
        }
    }

    fun author(name: String, url: String? = null, iconUrl: String? = null) {
        spec.setAuthor(name, url, iconUrl)
    }

    fun author(authorSpec: EmbedAuthor.() -> Unit) {
        EmbedAuthor().apply(authorSpec).apply {
            author(name, url, iconUrl)
        }
    }

    fun field(name: String, value: String, inline: Boolean = false) {
        spec.addField(name, value, inline)
    }

    fun field(fieldSpec: EmbedField.() -> Unit) {
        EmbedField().apply(fieldSpec).apply {
            field(name, value, inline)
        }
    }

    class EmbedAuthor internal constructor() {
        lateinit var name: String
        var url: String? = null
        var iconUrl: String? = null
    }

    class EmbedFooter internal constructor() {
        lateinit var text: String
        var iconUrl: String? = null
    }
    class EmbedField internal constructor() {
        lateinit var name: String
        lateinit var value: String
        var inline: Boolean = false
    }
}
