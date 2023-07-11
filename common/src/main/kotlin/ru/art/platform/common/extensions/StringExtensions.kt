package ru.art.platform.common.extensions

import com.google.common.hash.Hashing
import ru.art.core.constants.StringConstants.*
import ru.art.core.context.Context

fun String.normalizeNameToId() = toLowerCase().trim().replace(SPACE, DASH).replace(SLASH, DASH).replace(DOT, DASH)

fun String.crc32() = Hashing.crc32().hashString(this, Context.contextConfiguration().charset).toString()
