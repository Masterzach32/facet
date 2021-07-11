package io.facet.discord.dsl

import discord4j.common.annotations.*
import discord4j.core.spec.*
import discord4j.rest.util.*
import java.time.*

/**
 * Creates a new [EmbedCreateSpec] using an [EmbedBuilder].
 */
fun embed(block: EmbedBuilder.() -> Unit): EmbedCreateSpec = EmbedBuilder().apply(block).build()

/**
 * Edit an [EmbedCreateSpec] using an [EmbedBuilder]
 */
@Experimental
@Deprecated("", ReplaceWith("and(buildBlock)"))
fun EmbedCreateSpec.andThen(block: EmbedBuilder.() -> Unit): EmbedCreateSpec = and(block)

/**
 * Edit an [EmbedCreateSpec] using an [EmbedBuilder]
 */
@Experimental
fun EmbedCreateSpec.and(block: EmbedBuilder.() -> Unit): EmbedCreateSpec =
    EmbedBuilder(this).apply(block).build()

class EmbedBuilder internal constructor(
    private val builder: EmbedCreateSpec.Builder = EmbedCreateSpec.builder()
) : SpecBuilder<EmbedCreateSpec> {

    internal constructor(spec: EmbedCreateSpec) : this(EmbedCreateSpec.builder().from(spec))

    var title: String
        get() = build().title().get()
        set(value) = builder.title(value).let {}

    var description: String
        get() = build().description().get()
        set(value) = builder.description(value).let {}

    var url: String
        get() = build().url().get()
        set(value) = builder.url(value).let {}

    var timestamp: Instant
        get() = build().timestamp().get()
        set(value) = builder.timestamp(value).let {}

    var color: Color
        get() = build().color().get()
        set(value) = builder.color(value).let {}

    @Deprecated("Use \"image\"")
    var imageUrl: String
        get() = image
        set(value) = let { image = value }

    var image: String
        get() = build().image().get()
        set(value) = builder.image(value).let {}

    @Deprecated("Use \"thumbnail\"")
    var thumbnailUrl: String
        get() = thumbnail
        set(value) = let { thumbnail = value }

    var thumbnail: String
        get() = build().thumbnail().get()
        set(value) = builder.thumbnail(value).let {}

    fun footer(text: String, icon: String? = null) {
        builder.footer(EmbedCreateFields.Footer.of(text, icon))
    }

    fun footer(footerSpec: EmbedFooter.() -> Unit) {
        EmbedFooter().apply(footerSpec).apply {
            footer(text, iconUrl)
        }
    }

    fun author(name: String, url: String? = null, iconUrl: String? = null) {
        builder.author(EmbedCreateFields.Author.of(name, url, iconUrl))
    }

    fun author(authorSpec: EmbedAuthor.() -> Unit) {
        EmbedAuthor().apply(authorSpec).apply {
            author(name, url, iconUrl)
        }
    }

    fun field(name: String, value: String, inline: Boolean = false) {
        builder.addField(EmbedCreateFields.Field.of(name, value, inline))
    }

    fun field(fieldSpec: EmbedField.() -> Unit) {
        EmbedField().apply(fieldSpec).apply {
            field(name, value, inline)
        }
    }

    override fun build(): EmbedCreateSpec = builder.build()

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
