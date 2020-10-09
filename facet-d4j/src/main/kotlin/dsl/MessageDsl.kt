package io.facet.discord.dsl

import discord4j.common.util.*
import discord4j.core.spec.*
import discord4j.discordjson.json.*
import discord4j.rest.util.*
import io.facet.discord.extensions.*
import java.io.*
import java.util.function.*

/**
 * Creates a new [MessageTemplate] using a [MessageBuilder].
 */
inline fun message(block: MessageBuilder.() -> Unit): MessageTemplate = MessageBuilder()
    .apply(block)
    .toTemplate()

class MessageTemplate(
    private val data: MultipartRequest
) : Consumer<MessageCreateSpec>, (MessageCreateSpec) -> Unit {

    override fun accept(spec: MessageCreateSpec) = spec.populateFromData(data)

    override fun invoke(spec: MessageCreateSpec) = accept(spec)

    inline fun andThen(spec: MessageBuilder.() -> Unit): MessageTemplate = message {
        accept(this.spec)
        spec()
    }
}

class MessageBuilder(val spec: MessageCreateSpec = MessageCreateSpec()) {

    var content: String = ""
        set(value) = spec.setContent(value).let { field = value }

    var nonce: Snowflake = Snowflake.of(Snowflake.DISCORD_EPOCH)
        set(value) = spec.setNonce(value).let { field = value }

    var tts: Boolean = false
        set(value) = spec.setTts(value).let { field = value }

    inline fun embed(dsl: EmbedBuilder.() -> Unit) {
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

    fun toTemplate() = MessageTemplate(spec.asRequest())
}

private fun MessageCreateSpec.populateFromData(request: MultipartRequest) {
    request.createRequest?.content()?.nullable?.let(this::setContent)
    request.createRequest?.tts()?.nullable?.let(this::setTts)
    request.createRequest?.embed()?.nullable?.let { embed -> setEmbed { it.populateFromData(embed) } }
    request.files.forEach { file -> addFile(file.t1, file.t2) }
    request.createRequest?.allowedMentions()?.nullable?.let { data -> setAllowedMentions(populateFromData(data)) }
}

class AllowedMentionsBuilderDsl(private val builder: AllowedMentions.Builder) {

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

private fun populateFromData(data: AllowedMentionsData): AllowedMentions {
    val builder = AllowedMentions.builder()
    data.parse().nullable?.map { parseType ->
        when (parseType) {
            "roles" -> AllowedMentions.Type.ROLE
            "users" -> AllowedMentions.Type.USER
            "everyone" -> AllowedMentions.Type.EVERYONE_AND_HERE
            else -> error("Unexpected parse type: $parseType")
        }
    }?.toTypedArray()?.let(builder::parseType)
    data.roles().nullable?.map(Snowflake::of)?.toTypedArray()?.let(builder::allowRole)
    data.users().nullable?.map(Snowflake::of)?.toTypedArray()?.let(builder::allowUser)

    return builder.build()
}
