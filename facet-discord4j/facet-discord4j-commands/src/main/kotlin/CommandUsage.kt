package io.facet.discord.commands

class CommandUsage(usageMap: Map<String, String>) : Iterable<CommandUsage.Pair> {

    private val usageMap = usageMap.map { (example, desc) -> Pair(example, desc) }

    override fun iterator(): Iterator<Pair> = usageMap.iterator()

    data class Pair(val example: String, val description: String)
}

class CommandUsageBuilder {

    internal val usageMap = mutableMapOf<String, String>()

    fun default(description: String) {
        usageMap[""] = description
    }

    fun add(example: String, description: String) {
        usageMap[example] = description
    }
}

/**
 * **Note:** this DSL is experimental and subject to change.
 */
fun commandUsage(block: CommandUsageBuilder.() -> Unit) = CommandUsage(CommandUsageBuilder().apply(block).usageMap)
