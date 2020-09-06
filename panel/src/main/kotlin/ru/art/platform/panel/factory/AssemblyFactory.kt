package ru.art.platform.panel.factory

import ru.art.platform.api.model.assembly.*
import ru.art.platform.api.model.log.Log.*
import ru.art.platform.api.model.request.*
import ru.art.platform.panel.repository.AssemblyConfigurationRepository.getAssemblyConfiguration
import ru.art.platform.panel.repository.LogRepository.putLog

object AssemblyFactory {
    fun createAssembly(request: BuildRequest): Assembly = Assembly.builder()
            .artifactConfigurations(request.artifactConfigurations)
            .technology(getAssemblyConfiguration(request.projectId).technology)
            .projectId(request.projectId)
            .resourceId(request.resourceId)
            .version(request.version)
            .logId(putLog(builder().build()).id)
            .build()
}
