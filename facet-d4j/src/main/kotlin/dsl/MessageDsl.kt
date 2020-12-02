package io.facet.discord.dsl

import discord4j.common.util.*
import discord4j.core.spec.*
import discord4j.rest.util.*
import java.io.*

/**
 * Creates a new [MessageTemplate] using a [MessageBuilder].
 */
fun message(block: MessageBuilder.() -> Unit): MessageTemplate = MessageTemplate(block)

class MessageTemplate(
    private val template: MessageBuilder.() -> Unit
) : Template<MessageCreateSpec> {

    override fun accept(spec: MessageCreateSpec) = MessageBuilder(spec).run(template)

    override fun invoke(spec: MessageCreateSpec) = accept(spec)

    fun andThen(spec: MessageBuilder.() -> Unit): MessageTemplate = message {
        template()
        spec()
    }
}

class MessageBuilder(
    override val spec: MessageCreateSpec = MessageCreateSpec()
) : TemplateBuilder<MessageCreateSpec> {

    var content: String = ""
        set(value) = spec.setContent(value).let { field = value }

    var nonce: Snowflake = Snowflake.of(Snowflake.DISCORD_EPOCH)
        set(value) = spec.setNonce(value).let { field = value }

    var tts: Boolean = false
        set(value) = spec.setTts(value).let { field = value }

    var messageReference: Snowflake? = null
        set(value) = spec.setMessageReference(value).let { field = value }

    fun embed(dsl: EmbedBuilder.() -> Unit) {
        spec.setEmbed(io.facet.discord.dsl.embed(dsl))
    }

    fun file(fileName: String, file: InputStream, spoiler: Boolean = false) {
        if (spoiler)
            spec.addFileSpoiler(fileName, file)
        else
            spec.addFile(fileName, file)
    }

    inline fun allowedMentions(dsl: AllowedMentionsBuilderDsl.() -> Unit) {
        spec.setAllowedMentions(AllowedMentionsBuilderDsl(AllowedMentions.builder()).apply(dsl).build())
    }
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
