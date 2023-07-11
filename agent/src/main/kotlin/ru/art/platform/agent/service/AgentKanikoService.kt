package ru.art.platform.agent.service

import ru.art.core.constants.StringConstants.SLASH
import ru.art.core.constants.SystemConstants.PROCESS_ERROR_CODE_OK
import ru.art.platform.agent.constants.DockerConstants.DOCKER_CONFIG_ENVIRONMENT
import ru.art.platform.common.service.ProcessOutputListener
import ru.art.platform.common.service.process
import java.nio.file.Path

data class KanikoExecutorConfiguration(val contextPath: Path, val command: List<String>, val dockerConfigPath: String)

object AgentKanikoService {
    fun executeKaniko(configuration: KanikoExecutorConfiguration, listener: ProcessOutputListener = ProcessOutputListener()): Process {
        with(configuration) {
            val process = process(command, contextPath)
                    .environment(DOCKER_CONFIG_ENVIRONMENT, dockerConfigPath.substringBeforeLast(SLASH))
                    .onOutput(listener::produceEvent)
                    .onError(listener::produceError)
                    .execute()

            if (process.exitValue() != PROCESS_ERROR_CODE_OK) {
                listener.produceError("Kaniko failed with error code: ${process.exitValue()}")
                return process
            }

            listener.produceEvent("Kaniko finished with exit code: ${process.exitValue()}")
            return process
        }

    }
}