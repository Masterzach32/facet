package io.discordcommons.core

/**
 * Key for an object that allows for type-safe code.
 */
class AttributeKey<T : Any>(val name: String) : Comparable<AttributeKey<T>> {

    override fun toString() = name

    override fun compareTo(other: AttributeKey<T>) = name.compareTo(other.name)
}
