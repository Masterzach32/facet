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
public fun message(block: MessageBuilder.() -> Unit): MessageCreateSpec = MessageBuilder().apply(block).build()

/**
 * Edit an [MessageCreateSpec] using an [MessageBuilder]
 */
@Experimental
@Deprecated("", ReplaceWith("and(buildBlock)"))
public fun MessageCreateSpec.andThen(block: MessageBuilder.() -> Unit): MessageCreateSpec = and(block)

/**
 * Edit an [MessageCreateSpec] using an [MessageBuilder]
 */
@Experimental
public fun MessageCreateSpec.and(block: MessageBuilder.() -> Unit): MessageCreateSpec =
    MessageBuilder(this).apply(block).build()


public class MessageBuilder internal constructor(
    private val builder: MessageCreateSpec.Builder = MessageCreateSpec.builder()
) : SpecBuilder<MessageCreateSpec> {

    internal constructor(spec: MessageCreateSpec) : this(MessageCreateSpec.builder().from(spec))

    public var content: String
        get() = build().content().get()
        set(value) = builder.content(value).let {}

    public var nonce: String
        get() = build().nonce().get()
        set(value) = builder.nonce(value).let {}

    public var tts: Boolean
        get() = build().tts().get()
        set(value) = builder.tts(value).let {}

    public var messageReference: Snowflake
        get() = build().messageReference().get()
        set(value) = builder.messageReference(value).let {}

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

    override fun build(): MessageCreateSpec = builder.build()
}

public class AllowedMentionsBuilderDsl(private val builder: AllowedMentions.Builder) {

    public var repliedUser: Boolean = true
        set(value) = builder.repliedUser(value).let { field = value }

    public fun users(vararg id: Snowflake) {
        builder.allowUser(*id)
    }

    public fun parseType(vararg type: AllowedMentions.Type) {
        builder.parseType(*type)
    }

    public fun role(vararg id: Snowflake) {
        builder.allowRole(*id)
    }

    public fun build(): AllowedMentions = builder.build()
}
