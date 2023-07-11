package ru.art.platform.common.extensions

import ru.art.core.constants.DateConstants.YYYY_MM_DD_HH_MM_SS_24H_DASH
import java.time.Instant.now
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter.ofPattern

fun formatLogRecord(record: String) = "[${ofPattern(YYYY_MM_DD_HH_MM_SS_24H_DASH).format(now().atZone(systemDefault()))}]: $record"
