package ru.art.platform.common.extractor

import ru.art.core.constants.StringConstants.*

fun extractHostName(url: String) = url.substringAfter(SCHEME_DELIMITER).substringBeforeLast(COLON).substringBefore(SLASH)

fun extractPath(url: String, defaultPath: String = EMPTY_STRING) = url.substringAfter(SCHEME_DELIMITER).substringAfter(SLASH, defaultPath)
        .let { path ->
            if (path != defaultPath) {
                return@let SLASH + path
            }
            return@let path
        }