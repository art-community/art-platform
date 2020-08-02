package ru.art.platform.agent.extension

import ru.art.core.constants.StringConstants.*
import ru.art.platform.agent.constants.IgnoringDirectories.IGNORING_DIRECTORIES
import java.nio.file.*
import java.util.stream.Collectors
import java.util.stream.Collectors.*
import kotlin.streams.asStream
import kotlin.streams.toList

fun Path.extractName() = toAbsolutePath().toString().substringAfterLast(SLASH)

fun Path.findParentDirectoriesOfFilesStartsWith(name: String, filter: (Path) -> Boolean = { true }) = toFile()
        .walkTopDown()
        .onEnter { directory -> !IGNORING_DIRECTORIES.contains(directory.name)}
        .filter { file ->
            try {
                file.isFile && file.name.startsWith(name) && filter(file.toPath())
            } catch (ignored: Throwable) {
                false
            }
        }
        .map { file -> file.parentFile.toPath() }

fun Path.firstFileStartsWith(name: String, filter: (Path) -> Boolean = { true }): Path? = toFile()
        .walkTopDown()
        .firstOrNull { file ->
            try {
                file.isFile && file.name.startsWith(name) && filter(file.toPath())
            } catch (ignored: Throwable) {
                false
            }
        }
        ?.toPath()

fun Path.listFiles() = toFile()
        .walkTopDown()
        .filter { file ->
            try {
                file.isFile
            } catch (e: Throwable) {
                false
            }
        }
