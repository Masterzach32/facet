package io.facet.discord.dsl

import discord4j.common.annotations.*
import discord4j.core.`object`.component.*
import discord4j.core.spec.*
import discord4j.rest.util.*
import java.io.*

/**
 * Creates a new [WebhookExecuteSpec] using a [WebhookMessageBuilder].
 */
fun webhookMessage(buildBlock: WebhookMessageBuilder.() -> Unit): WebhookExecuteSpec =
    WebhookMessageBuilder().apply(buildBlock).build()

/**
 * Creates a new [WebhookExecuteSpec] using an [EmbedBuilder].
 */
fun webhookMessageEmbed(buildBlock: EmbedBuilder.() -> Unit): WebhookExecuteSpec =
    WebhookExecuteSpec.create().withEmbeds(EmbedBuilder().apply(buildBlock).build())

/**
 * Edit a [WebhookExecuteSpec] using an [WebhookMessageBuilder]
 */
@Experimental
fun WebhookExecuteSpec.and(buildBlock: WebhookMessageBuilder.() -> Unit): WebhookExecuteSpec =
    WebhookMessageBuilder(this).apply(buildBlock).build()

class WebhookMessageBuilder internal constructor(
    private val builder: WebhookExecuteSpec.Builder = WebhookExecuteSpec.builder()
) : SpecBuilder<WebhookExecuteSpec> {

    internal constructor(spec: WebhookExecuteSpec) : this(WebhookExecuteSpec.builder().from(spec))

    var content: String
        get() = build().content().get()
        set(value) = builder.content(value).let {}

    var username: String
        get() = build().username().get()
        set(value) = builder.username(value).let {}

    var avatarUrl: String
        get() = build().avatarUrl().get()
        set(value) = builder.avatarUrl(value).let {}

    var tts: Boolean
        get() = build().tts()
        set(value) = builder.tts(value).let {}

    fun component(component: LayoutComponent) {
        builder.addComponent(component)
    }

    fun actionRow(vararg components: ActionComponent) {
        builder.addComponent(ActionRow.of(*components))
    }

    fun embed(spec: EmbedCreateSpec) {
        builder.addEmbed(spec)
    }

    fun embed(dsl: EmbedBuilder.() -> Unit) {
        builder.addEmbed(io.facet.discord.dsl.embed(dsl))
    }

    fun file(fileName: String, file: InputStream, spoiler: Boolean = false) {
        if (spoiler)
            builder.addFileSpoiler(MessageCreateFields.FileSpoiler.of(fileName, file))
        else
            builder.addFile(MessageCreateFields.File.of(fileName, file))
    }

    fun allowedMentions(dsl: AllowedMentionsBuilderDsl.() -> Unit) {
        builder.allowedMentions(AllowedMentionsBuilderDsl(AllowedMentions.builder()).apply(dsl).build())
    }

    override fun build(): WebhookExecuteSpec = builder.build()
}
