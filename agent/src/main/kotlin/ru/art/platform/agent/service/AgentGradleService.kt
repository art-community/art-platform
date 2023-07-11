package ru.art.platform.agent.service

import ru.art.core.constants.StringConstants.*
import ru.art.core.constants.SystemConstants.PROCESS_ERROR_CODE_OK
import ru.art.platform.agent.constants.GradleConstants.GRADLE_PROJECT_DIR_PROPERTY_KEYWORD
import ru.art.platform.agent.constants.GradleConstants.GRADLE_PROJECT_METHOD_KEYWORD
import ru.art.platform.agent.constants.GradleConstants.GRADLE_SETTINGS_FILE
import ru.art.platform.agent.constants.GradleConstants.GRADLE_STOP
import ru.art.platform.agent.constants.TechnologyFiles.BUILD_GRADLE_FILE
import ru.art.platform.agent.extension.extractName
import ru.art.platform.agent.extension.findParentDirectoriesOfFilesStartsWith
import ru.art.platform.agent.extension.firstFileStartsWith
import ru.art.platform.common.extensions.normalizeNameToId
import ru.art.platform.common.service.ProcessOutputListener
import ru.art.platform.common.service.process
import java.nio.file.Path

data class GradleBuildConfiguration(val projectPath: Path, val executable: String, val command: List<String>)

data class GradleModule(val name: String, val path: Path)

object AgentGradleService {
    fun executeGradleBuild(configuration: GradleBuildConfiguration, listener: ProcessOutputListener = ProcessOutputListener()): Process {
        val process = process(configuration.command, configuration.projectPath)
                .onOutput(listener::produceEvent)
                .onError { error -> listener.produceError(error) }
                .execute()


        if (process.exitValue() != PROCESS_ERROR_CODE_OK) {
            listener.produceError("Gradle failed with error code: ${process.exitValue()}")
            process(listOf(configuration.executable, GRADLE_STOP), configuration.projectPath).execute()
            return process
        }

        listener.produceEvent("Gradle finished with exit code: ${process.exitValue()}")
        process(listOf(configuration.executable, GRADLE_STOP), configuration.projectPath).execute()
        return process
    }

    fun findGradleModules(projectPath: Path): Sequence<GradleModule> {
        val settings = projectPath.firstFileStartsWith(GRADLE_SETTINGS_FILE)?.let { settingsFile ->
            settingsFile.toFile()
                    .readText()
                    .let { text -> (text.split(NEW_LINE) + text.split(SEMICOLON)).toSet() }
        }

        return projectPath
                .findParentDirectoriesOfFilesStartsWith(BUILD_GRADLE_FILE) { file ->
                    file.parent.toAbsolutePath().toString() != projectPath.toAbsolutePath().toString()
                }
                .map { modulePath ->
                    settings?.firstOrNull { line -> line.contains(modulePath.extractName().normalizeNameToId()) && line.contains(GRADLE_PROJECT_DIR_PROPERTY_KEYWORD) }
                            ?.substringAfter(GRADLE_PROJECT_METHOD_KEYWORD)
                            ?.substringBefore(GRADLE_PROJECT_DIR_PROPERTY_KEYWORD)
                            ?.replace(OPENING_BRACKET, EMPTY_STRING)
                            ?.replace(CLOSING_BRACKET, EMPTY_STRING)
                            ?.replace(SINGLE_QUOTE, EMPTY_STRING)
                            ?.replace(COLON, EMPTY_STRING)
                            ?.let { name -> GradleModule(name = name, path = modulePath) }
                            ?: GradleModule(name = modulePath.extractName().normalizeNameToId(), path = modulePath)
                }
    }
}
