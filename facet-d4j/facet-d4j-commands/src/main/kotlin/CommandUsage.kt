package io.facet.discord.commands

public class CommandUsage(usageMap: Map<String, String>) : Iterable<CommandUsage.Pair> {

    private val usageMap = usageMap.map { (example, desc) -> Pair(example, desc) }

    override fun iterator(): Iterator<Pair> = usageMap.iterator()

    public data class Pair(val example: String, val description: String)
}

public class CommandUsageBuilder {

    internal val usageMap = mutableMapOf<String, String>()

    public fun default(description: String) {
        usageMap[""] = description
    }

    public fun add(example: String, description: String) {
        usageMap[example] = description
    }
}

/**
 * **Note:** this DSL is experimental and subject to change.
 */
public fun commandUsage(block: CommandUsageBuilder.() -> Unit): CommandUsage = CommandUsage(CommandUsageBuilder().apply(block).usageMap)
