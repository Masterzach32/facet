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

    public var title: String
        get() = build().title().get()
        set(value) = builder.title(value).let {}

    public var description: String
        get() = build().description().get()
        set(value) = builder.description(value).let {}

    public var url: String
        get() = build().url().get()
        set(value) = builder.url(value).let {}

    public var timestamp: Instant
        get() = build().timestamp().get()
        set(value) = builder.timestamp(value).let {}

    public var color: Color
        get() = build().color().get()
        set(value) = builder.color(value).let {}

    @Deprecated("Use \"image\"")
    public var imageUrl: String
        get() = image
        set(value) = let { image = value }

    public var image: String
        get() = build().image().get()
        set(value) = builder.image(value).let {}

    @Deprecated("Use \"thumbnail\"")
    public var thumbnailUrl: String
        get() = thumbnail
        set(value) = let { thumbnail = value }

    public var thumbnail: String
        get() = build().thumbnail().get()
        set(value) = builder.thumbnail(value).let {}

    public fun footer(text: String, icon: String? = null) {
        builder.footer(EmbedCreateFields.Footer.of(text, icon))
    }

    public fun footer(footerSpec: EmbedFooter.() -> Unit) {
        EmbedFooter().apply(footerSpec).apply {
            footer(text, iconUrl)
        }
    }

    public fun author(name: String, url: String? = null, iconUrl: String? = null) {
        builder.author(EmbedCreateFields.Author.of(name, url, iconUrl))
    }

    public fun author(authorSpec: EmbedAuthor.() -> Unit) {
        EmbedAuthor().apply(authorSpec).apply {
            author(name, url, iconUrl)
        }
    }

    public fun field(name: String, value: String, inline: Boolean = false) {
        builder.addField(EmbedCreateFields.Field.of(name, value, inline))
    }

    public fun field(fieldSpec: EmbedField.() -> Unit) {
        EmbedField().apply(fieldSpec).apply {
            field(name, value, inline)
        }
    }

    override fun build(): EmbedCreateSpec = builder.build()

    public class EmbedAuthor internal constructor() {
        public lateinit var name: String
        public var url: String? = null
        public var iconUrl: String? = null
    }

    public class EmbedFooter internal constructor() {
        public lateinit var text: String
        public var iconUrl: String? = null
    }

    public class EmbedField internal constructor() {
        public lateinit var name: String
        public lateinit var value: String
        public var inline: Boolean = false
    }
}
