/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.facet.discord.dsl

import discord4j.common.annotations.*
import discord4j.core.spec.*
import discord4j.rest.util.*
import java.time.*

/**
 * Creates a new [EmbedCreateSpec] using an [EmbedBuilder].
 */
public fun embed(block: EmbedBuilder.() -> Unit): EmbedCreateSpec = EmbedBuilder().apply(block).build()

/**
 * Edit an [EmbedCreateSpec] using an [EmbedBuilder]
 */
@Experimental
@Deprecated("", ReplaceWith("and(buildBlock)"))
public fun EmbedCreateSpec.andThen(block: EmbedBuilder.() -> Unit): EmbedCreateSpec = and(block)

/**
 * Edit an [EmbedCreateSpec] using an [EmbedBuilder]
 */
@Experimental
public infix fun EmbedCreateSpec.and(block: EmbedBuilder.() -> Unit): EmbedCreateSpec =
    EmbedBuilder(this).apply(block).build()

public class EmbedBuilder internal constructor(
    private val builder: EmbedCreateSpec.Builder = EmbedCreateSpec.builder()
) : SpecBuilder<EmbedCreateSpec> {

    internal constructor(spec: EmbedCreateSpec) : this(EmbedCreateSpec.builder().from(spec))

    /**
     * The title for this embed.
     */
    public var title: String
        get() = build().title().get()
        set(value) = builder.title(value).let {}

    /**
     * The description for this embed.
     */
    public var description: String
        get() = build().description().get()
        set(value) = builder.description(value).let {}

    /**
     * The url for this embed.
     */
    public var url: String
        get() = build().url().get()
        set(value) = builder.url(value).let {}

    /**
     * The timestamp for this embed.
     */
    public var timestamp: Instant
        get() = build().timestamp().get()
        set(value) = builder.timestamp(value).let {}

    /**
     * The color for this embed.
     */
    public var color: Color
        get() = build().color().get()
        set(value) = builder.color(value).let {}

    @Deprecated("Use \"image\"")
    public var imageUrl: String
        get() = image
        set(value) = let { image = value }

    /**
     * The image url for this embed.
     */
    public var image: String
        get() = build().image().get()
        set(value) = builder.image(value).let {}

    @Deprecated("Use \"thumbnail\"")
    public var thumbnailUrl: String
        get() = thumbnail
        set(value) = let { thumbnail = value }

    /**
     * The thumbnail url for this embed.
     */
    public var thumbnail: String
        get() = build().thumbnail().get()
        set(value) = builder.thumbnail(value).let {}

    /**
     * Sets the title for this embed.
     */
    public fun title(title: String) {
        builder.title(title)
    }

    /**
     * Sets the description for this embed.
     */
    public fun description(description: String) {
        builder.description(description)
    }

    /**
     * Sets the url for this embed.
     */
    public fun url(url: String) {
        builder.url(url)
    }

    /**
     * Sets the timestamp for this embed.
     */
    public fun timestamp(timestamp: Instant) {
        builder.timestamp(timestamp)
    }

    /**
     * Sets the color for this embed.
     */
    public fun color(color: Color) {
        builder.color(color)
    }

    /**
     * Sets the image for this embed.
     */
    public fun image(url: String) {
        builder.image(url)
    }

    /**
     * Sets the thumbnail for this embed.
     */
    public fun thumbnail(url: String) {
        builder.thumbnail(url)
    }

    /**
     * Sets the footer for this embed.
     */
    public fun footer(text: String, icon: String? = null) {
        builder.footer(EmbedCreateFields.Footer.of(text, icon))
    }

    /**
     * Sets the footer for this embed.
     */
    public fun footer(footerSpec: EmbedFooter.() -> Unit) {
        EmbedFooter().apply(footerSpec).apply {
            footer(text, iconUrl)
        }
    }

    /**
     * Sets the author for this embed.
     */
    public fun author(name: String, url: String? = null, iconUrl: String? = null) {
        builder.author(EmbedCreateFields.Author.of(name, url, iconUrl))
    }

    /**
     * Sets the author for this embed.
     */
    public fun author(authorSpec: EmbedAuthor.() -> Unit) {
        EmbedAuthor().apply(authorSpec).apply {
            author(name, url, iconUrl)
        }
    }

    /**
     * Adds a field to this embed.
     */
    public fun field(name: String, value: String, inline: Boolean = false) {
        builder.addField(EmbedCreateFields.Field.of(name, value, inline))
    }

    /**
     * Adds a field to this embed.
     */
    public fun field(fieldSpec: EmbedField.() -> Unit) {
        EmbedField().apply(fieldSpec).apply {
            field(name, value, inline)
        }
    }

    override fun build(): EmbedCreateSpec = builder.build()

    /**
     * Embed author field builder
     */
    public class EmbedAuthor internal constructor() {

        /**
         * The name of the author.
         */
        public lateinit var name: String

        /**
         * The URL for this author.
         */
        public var url: String? = null

        /**
         * The icon URL for this author.
         */
        public var iconUrl: String? = null

        /**
         * Sets the name for this author.
         */
        public fun name(name: String) {
            this.name = name
        }

        /**
         * Sets the URL for this author.
         */
        public fun url(url: String) {
            this.url = url
        }

        /**
         * Sets the icon URL for this author.
         */
        public fun iconUrl(iconUrl: String) {
            this.iconUrl = iconUrl
        }
    }

    public class EmbedFooter internal constructor() {

        /**
         * The text shown in the embed footer.
         */
        public lateinit var text: String

        /**
         * The URL for the icon shown in the embed footer.
         */
        public var iconUrl: String? = null

        /**
         * Sets the text for this footer.
         */
        public fun text(text: String) {
            this.text = text
        }

        /**
         * Sets the icon URL for this footer
         */
        public fun iconUrl(iconUrl: String) {
            this.iconUrl = iconUrl
        }
    }

    public class EmbedField internal constructor() {

        /**
         * The name of this field.
         */
        public lateinit var name: String

        /**
         * The value of this field.
         */
        public lateinit var value: String

        /**
         * Whether this field should be in line with other fields in the embed.
         */
        public var inline: Boolean = false

        /**
         * Sets the name of this field
         */
        public fun name(name: String) {
            this.name = name
        }

        /**
         * Sets the value of this field.
         */
        public fun value(value: String) {
            this.value = value
        }

        /**
         * Sets whether this field should be inlined.
         */
        public fun inline(inline: Boolean = true) {
            this.inline = inline
        }
    }
}
