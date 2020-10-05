package io.facet.core.util

fun utf16(code: Int): String = String(Character.toChars(code))

val Int.unicode: String
    get() = utf16(this)
