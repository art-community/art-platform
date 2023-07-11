package ru.art.platform.agent.cache

import ru.art.logging.LoggingModule.*
import ru.art.platform.agent.constants.CacheConstants.GRADLE_CACHE_COMMAND
import ru.art.platform.agent.constants.CacheConstants.GRADLE_CACHE_DATA_DIR_ARGUMENT
import ru.art.platform.agent.constants.CacheConstants.GRADLE_CACHE_DIRECTORY
import ru.art.platform.agent.constants.CacheConstants.GRADLE_CACHE_PORT_ARGUMENT
import ru.art.platform.agent.module.*
import ru.art.platform.common.constants.CommonConstants.*
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.common.service.*
import java.lang.System.*
import java.nio.file.Files.*
import java.nio.file.Paths.*

fun startCacheServer() {
    if (getenv().contains(GRADLE_CACHE_PROPERTY) && getenv().contains(GRADLE_CACHE_PORT_PROPERTY)) {
        val command = mutableListOf(*GRADLE_CACHE_COMMAND,
                GRADLE_CACHE_DATA_DIR_ARGUMENT, createDirectories(get(GRADLE_CACHE_DIRECTORY)).toAbsolutePath().toString(),
                GRADLE_CACHE_PORT_ARGUMENT, getenv(GRADLE_CACHE_PORT_PROPERTY))
        process(command, get("/$AGENT_CAMEL_CASE")).log(loggingModule().getLogger(AgentModule::class.java)).run()
    }
}