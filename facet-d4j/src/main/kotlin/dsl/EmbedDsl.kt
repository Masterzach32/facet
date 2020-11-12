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
inline fun embed(block: EmbedBuilder.() -> Unit): EmbedTemplate = EmbedBuilder()
    .apply(block)
    .toTemplate()

class EmbedTemplate(
    private val data: EmbedData
) : Consumer<EmbedCreateSpec>, (EmbedCreateSpec) -> Unit {

    override fun accept(spec: EmbedCreateSpec) = spec.populateFromData(data)

    override fun invoke(spec: EmbedCreateSpec) = accept(spec)

    inline fun andThen(spec: EmbedBuilder.() -> Unit): EmbedTemplate = embed {
        accept(this.spec)
        spec()
    }
}

class EmbedBuilder(
    override val spec: EmbedCreateSpec = EmbedCreateSpec()
) : TemplateBuilder<EmbedCreateSpec, EmbedTemplate> {

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

    override fun toTemplate() = EmbedTemplate(spec.asRequest())

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

internal fun EmbedCreateSpec.populateFromData(request: EmbedData) {
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

//class EmbedBuilder : TemplateBuilder<EmbedTemplate> {
//
//    private var footer: EmbedFooter? = null
//    private var author: EmbedAuthor? = null
//    private val fields = mutableListOf<EmbedField>()
//
//    lateinit var title: String
//    lateinit var description: String
//    lateinit var url: String
//    lateinit var timestamp: Instant
//    lateinit var color: Color
//
//    lateinit var imageUrl: String
//    lateinit var thumbnailUrl: String
//
//    fun footer(text: String, iconUrl: String? = null) {
//        footer {
//            this.text = text
//            this.iconUrl = iconUrl
//        }
//    }
//
//    fun footer(footerSpec: EmbedFooter.() -> Unit) {
//        footer = EmbedFooter().apply(footerSpec)
//    }
//
//    fun author(name: String, url: String? = null, iconUrl: String? = null) {
//        author {
//            this.name = name
//            this.url = url
//            this.iconUrl = iconUrl
//        }
//    }
//
//    fun author(authorSpec: EmbedAuthor.() -> Unit) {
//        author = EmbedAuthor().apply(authorSpec)
//    }
//
//    fun field(name: String, value: String, inline: Boolean = false) {
//        field {
//            this.name = name
//            this.value = value
//            this.inline = inline
//        }
//    }
//
//    fun field(fieldSpec: EmbedField.() -> Unit) {
//        fields.add(EmbedField().apply(fieldSpec))
//    }
//
//    override fun toTemplate(): EmbedTemplate = EmbedTemplate(EmbedData.builder().apply {
//        if (this@EmbedBuilder::title.isInitialized)
//            title(title)
//        if (this@EmbedBuilder::description.isInitialized)
//            description(description)
//        if (this@EmbedBuilder::url.isInitialized)
//            url(url)
//        if (this@EmbedBuilder::timestamp.isInitialized)
//            timestamp(DateTimeFormatter.ISO_INSTANT.format(timestamp))
//        if (this@EmbedBuilder::color.isInitialized)
//            color(color.rgb)
//        if (this@EmbedBuilder::imageUrl.isInitialized)
//            image(EmbedImageData.builder().url(imageUrl).build())
//        if (this@EmbedBuilder::thumbnailUrl.isInitialized)
//            thumbnail(EmbedThumbnailData.builder().url(thumbnailUrl).build())
//        footer?.let { footer ->
//            footer(EmbedFooterData.builder()
//                .text(footer.text)
//                .iconUrl(if (footer.iconUrl != null) Possible.of(footer.iconUrl!!) else Possible.absent())
//                .build()
//            )
//        }
//        author?.let { author ->
//            author(EmbedAuthorData.builder()
//                .name(author.name)
//                .url(if (author.url != null) Possible.of(author.url!!) else Possible.absent())
//                .iconUrl(if (author.iconUrl != null) Possible.of(author.iconUrl!!) else Possible.absent())
//                .build()
//            )
//        }
//        fields(fields.map { EmbedFieldData.builder().name(it.name).value(it.value).inline(it.inline).build() })
//    }.build())
//
//
//    class EmbedAuthor internal constructor() {
//        lateinit var name: String
//        var url: String? = null
//        var iconUrl: String? = null
//    }
//
//    class EmbedFooter internal constructor() {
//        lateinit var text: String
//        var iconUrl: String? = null
//    }
//    class EmbedField internal constructor() {
//        lateinit var name: String
//        lateinit var value: String
//        var inline: Boolean = false
//    }
//}
