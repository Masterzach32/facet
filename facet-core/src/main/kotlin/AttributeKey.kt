package io.facet.core

/**
 * Key for an object that allows for type-safe code.
 */
public class AttributeKey<T : Any>(public val name: String) : Comparable<AttributeKey<T>> {

    override fun toString(): String = name

    override fun compareTo(other: AttributeKey<T>): Int = name.compareTo(other.name)
}
