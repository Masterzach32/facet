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

package io.facet.common.dsl

import discord4j.common.annotations.Experimental
import discord4j.core.`object`.component.ActionComponent
import discord4j.core.`object`.component.ActionRow
import discord4j.core.`object`.component.LayoutComponent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateFields
import discord4j.core.spec.WebhookExecuteSpec
import discord4j.rest.util.AllowedMentions
import java.io.InputStream

/**
 * Creates a new [WebhookExecuteSpec] using a [WebhookMessageBuilder].
 */
public fun webhookMessage(block: WebhookMessageBuilder.() -> Unit): WebhookExecuteSpec =
    WebhookMessageBuilder().apply(block).build()

/**
 * Creates a new [WebhookExecuteSpec] using an [EmbedBuilder].
 */
public fun webhookMessageEmbed(block: EmbedBuilder.() -> Unit): WebhookExecuteSpec =
    WebhookExecuteSpec.create().withEmbeds(EmbedBuilder().apply(block).build())

/**
 * Edit a [WebhookExecuteSpec] using an [WebhookMessageBuilder]
 */
@Experimental
public infix fun WebhookExecuteSpec.and(block: WebhookMessageBuilder.() -> Unit): WebhookExecuteSpec =
    WebhookMessageBuilder(this).apply(block).build()

public class WebhookMessageBuilder internal constructor(
    private val builder: WebhookExecuteSpec.Builder = WebhookExecuteSpec.builder()
) : SpecBuilder<WebhookExecuteSpec> {

    internal constructor(spec: WebhookExecuteSpec) : this(WebhookExecuteSpec.builder().from(spec))

    public var content: String
        get() = build().content().get()
        set(value) = builder.content(value).let {}

    public var username: String
        get() = build().username().get()
        set(value) = builder.username(value).let {}

    public var avatarUrl: String
        get() = build().avatarUrl().get()
        set(value) = builder.avatarUrl(value).let {}

    public var tts: Boolean
        get() = build().tts()
        set(value) = builder.tts(value).let {}

    public fun content(content: String) {
        builder.content(content)
    }

    public fun username(username: String) {
        builder.username(username)
    }

    public fun avatarUrl(avatarUrl: String) {
        builder.avatarUrl(avatarUrl)
    }

    public fun tts(tts: Boolean) {
        builder.tts(tts)
    }

    public fun component(component: LayoutComponent) {
        builder.addComponent(component)
    }

    public fun actionRow(vararg components: ActionComponent) {
        builder.addComponent(ActionRow.of(*components))
    }

    public fun embed(spec: EmbedCreateSpec) {
        builder.addEmbed(spec)
    }

    public fun embed(dsl: EmbedBuilder.() -> Unit) {
        builder.addEmbed(io.facet.common.dsl.embed(dsl))
    }

    public fun file(fileName: String, file: InputStream, spoiler: Boolean = false) {
        if (spoiler)
            builder.addFileSpoiler(MessageCreateFields.FileSpoiler.of(fileName, file))
        else
            builder.addFile(MessageCreateFields.File.of(fileName, file))
    }

    public fun allowedMentions(dsl: AllowedMentionsBuilderDsl.() -> Unit) {
        builder.allowedMentions(AllowedMentionsBuilderDsl(AllowedMentions.builder()).apply(dsl).build())
    }

    override fun build(): WebhookExecuteSpec = builder.build()
}
