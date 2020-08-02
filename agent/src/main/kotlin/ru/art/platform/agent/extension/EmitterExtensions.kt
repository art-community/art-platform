package ru.art.platform.agent.extension

import ru.art.core.colorizer.AnsiColorizer.*
import ru.art.platform.common.emitter.*
import ru.art.platform.common.service.*

fun Emitter<String>.outputListener() = ProcessOutputListener()
        .onError { line -> emit(error(line)) }
        .onEvent { line -> emit(line) }