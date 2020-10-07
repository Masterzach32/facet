package io.facet.discord.dsl

import discord4j.core.spec.*
import discord4j.discordjson.json.*
import discord4j.rest.util.*
import io.facet.discord.extensions.*
import java.time.*
import java.time.format.*
import java.util.function.*

/**
 * Creates a new [EmbedTemplate] using an [EmbedBuilder].
 */
inline fun embed(block: EmbedBuilder.() -> Unit) = EmbedBuilder(EmbedCreateSpec())
    .apply(block)
    .toTemplate()

class EmbedTemplate(private val data: EmbedData) : Consumer<EmbedCreateSpec>, (EmbedCreateSpec) -> Unit {

    override fun accept(spec: EmbedCreateSpec) = spec.fromData(data)

    override fun invoke(spec: EmbedCreateSpec) = accept(spec)

    inline fun andThen(spec: EmbedBuilder.() -> Unit): EmbedTemplate {
        return embed {
            accept(this.spec)
            spec()
        }
    }
}

class EmbedBuilder(val spec: EmbedCreateSpec) {

    var title: String = ""
        set(value) = spec.setTitle(value).let { field = value }
    @Deprecated("Use title property")
    fun title(value: String) {
        title = value
    }

    var description: String = ""
        set(value) = spec.setDescription(value).let { field = value }
    @Deprecated("Use description property")
    fun description(value: String) {
        description = value
    }

    var url: String = ""
        set(value) = spec.setUrl(value).let { field = value }
    @Deprecated("Use url property")
    fun url(value: String) {
        url = value
    }

    var timestamp: Instant = Instant.EPOCH
        set(value) = spec.setTimestamp(value).let { field = value }
    @Deprecated("Use timestamp property")
    fun timestamp(timestamp: Instant) {
        this.timestamp = timestamp
    }

    var color: Color = Color.WHITE
        set(value) = spec.setColor(value).let { field = value }
    @Deprecated("Use color property")
    fun color(color: Color) {
        this.color = color
    }

    fun footer(text: String, icon: String? = null) {
        spec.setFooter(text, icon)
    }

    var imageUrl: String = ""
        set(value) = spec.setImage(value).let { field = value }
    @Deprecated("Use imageUrl property")
    fun image(url: String) {
        imageUrl = url
    }

    var thumbnailUrl: String = ""
        set(value) = spec.setThumbnail(value).let { field = value }
    @Deprecated("Use thumbnailUrl property")
    fun thumbnail(url: String) {
        thumbnailUrl = url
    }

    fun author(name: String, url: String? = null, iconUrl: String? = null) {
        spec.setAuthor(name, url, iconUrl)
    }

    fun field(name: String, value: String, inline: Boolean = false) {
        spec.addField(name, value, inline)
    }

    fun toTemplate(): EmbedTemplate = EmbedTemplate(spec.asRequest())
}

internal fun EmbedCreateSpec.fromData(request: EmbedData) {
    request.title().nullable?.let { setTitle(it) }
    request.description().nullable?.let { setDescription(it) }
    request.url().nullable?.let { setUrl(it) }
    request.timestamp().nullable?.let { setTimestamp(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(it))) }
    request.color().nullable?.let { setColor(Color.of(it)) }
    request.footer().nullable?.let { setFooter(it.text(), it.iconUrl().nullable) }
    request.image().nullable?.url()?.nullable?.let { setImage(it) }
    request.thumbnail().nullable?.url()?.nullable?.let { setThumbnail(it) }
    request.author().nullable?.let { author ->
        author.name().nullable?.let { name ->
            setAuthor(name, author.url().nullable, author.iconUrl().nullable)
        }
    }
    request.fields().nullable?.forEach { field ->
        field.inline().nullable?.let { inline ->
            addField(field.name(), field.value(), inline)
        }
    }
}
