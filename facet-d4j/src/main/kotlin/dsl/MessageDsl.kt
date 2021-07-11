package io.facet.discord.dsl

import discord4j.common.annotations.*
import discord4j.common.util.*
import discord4j.core.`object`.component.*
import discord4j.core.spec.*
import discord4j.rest.util.*
import java.io.*

/**
 * Creates a new [MessageCreateSpec] using a [MessageBuilder].
 */
fun message(buildBlock: MessageBuilder.() -> Unit): MessageCreateSpec = MessageBuilder().apply(buildBlock).build()

/**
 * Edit an [MessageCreateSpec] using an [MessageBuilder]
 */
@Experimental
@Deprecated("", ReplaceWith("and(buildBlock)"))
fun MessageCreateSpec.andThen(buildBlock: MessageBuilder.() -> Unit): MessageCreateSpec = and(buildBlock)

/**
 * Edit an [MessageCreateSpec] using an [MessageBuilder]
 */
@Experimental
fun MessageCreateSpec.and(buildBlock: MessageBuilder.() -> Unit): MessageCreateSpec =
    MessageBuilder(this).apply(buildBlock).build()


class MessageBuilder internal constructor(
    private val builder: MessageCreateSpec.Builder = MessageCreateSpec.builder()
) : SpecBuilder<MessageCreateSpec> {

    internal constructor(spec: MessageCreateSpec) : this(MessageCreateSpec.builder().from(spec))

    var content: String
        get() = build().content().get()
        set(value) = builder.content(value).let {}

    var nonce: String
        get() = build().nonce().get()
        set(value) = builder.nonce(value).let {}

    var tts: Boolean
        get() = build().tts().get()
        set(value) = builder.tts(value).let {}

    var messageReference: Snowflake
        get() = build().messageReference().get()
        set(value) = builder.messageReference(value).let {}

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

    override fun build(): MessageCreateSpec = builder.build()
}

class AllowedMentionsBuilderDsl(private val builder: AllowedMentions.Builder) {

    var repliedUser: Boolean = true
        set(value) = builder.repliedUser(value).let { field = value }

    fun users(vararg id: Snowflake) {
        builder.allowUser(*id)
    }

    fun parseType(vararg type: AllowedMentions.Type) {
        builder.parseType(*type)
    }

    fun role(vararg id: Snowflake) {
        builder.allowRole(*id)
    }

    fun build(): AllowedMentions = builder.build()
}
