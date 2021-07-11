package io.facet.core.util

public fun utf16(code: Int): String = String(Character.toChars(code))

public val Int.unicode: String
    get() = utf16(this)
