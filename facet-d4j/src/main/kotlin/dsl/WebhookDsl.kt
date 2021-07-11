package io.facet.discord.dsl

import discord4j.common.annotations.*
import discord4j.core.`object`.component.*
import discord4j.core.spec.*
import discord4j.rest.util.*
import java.io.*

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
public fun WebhookExecuteSpec.and(block: WebhookMessageBuilder.() -> Unit): WebhookExecuteSpec =
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
        builder.addEmbed(io.facet.discord.dsl.embed(dsl))
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
