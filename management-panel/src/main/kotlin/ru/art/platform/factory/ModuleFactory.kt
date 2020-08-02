package ru.art.platform.factory

import ru.art.platform.api.model.external.ExternalIdentifier
import ru.art.platform.api.model.module.Module
import ru.art.platform.api.model.request.ModuleInstallationRequest
import ru.art.platform.api.model.request.ModuleUpdateRequest
import ru.art.platform.common.constants.States.MODULE_UPDATE_STARTED_STATE
import ru.art.platform.common.extensions.normalizeNameToId
import ru.art.platform.repository.ModuleRepository.getModule

object ModuleFactory {
    fun createModule(request: ModuleInstallationRequest): Module = Module.builder()
            .name(request.configuration.name)
            .projectId(request.projectId)
            .resourceId(request.configuration.resourceId)
            .externalId(ExternalIdentifier.builder()
                    .id(request.configuration.name.normalizeNameToId())
                    .resourceId(request.configuration.resourceId)
                    .build())
            .artifact(request.configuration.artifact)
            .url(request.configuration.url)
            .parameters(request.configuration.parameters)
            .count(request.configuration.count)
            .ports(request.configuration.ports)
            .preparedConfigurations(request.configuration.preparedConfigurations)
            .manualConfigurations(request.configuration.manualConfigurations)
            .additionalFiles(request.configuration.additionalFiles)
            .applications(request.configuration.applications)
            .build()

    fun createModule(currentModule: Module, request: ModuleUpdateRequest): Module = currentModule.toBuilder()
            .clearPortMappings()
            .clearPorts()
            .clearPreparedConfigurations()
            .clearManualConfigurations()
            .clearAdditionalFiles()
            .clearApplications()
            .name(request.newModuleConfiguration.name)
            .resourceId(request.newModuleConfiguration.resourceId)
            .externalId(ExternalIdentifier.builder()
                    .id(request.newModuleConfiguration.name)
                    .resourceId(request.newModuleConfiguration.resourceId)
                    .build())
            .state(MODULE_UPDATE_STARTED_STATE)
            .artifact(request.newModuleConfiguration.artifact)
            .url(request.newModuleConfiguration.url)
            .parameters(request.newModuleConfiguration.parameters)
            .count(request.newModuleConfiguration.count)
            .ports(request.newModuleConfiguration.ports)
            .portMappings(getModule(request.moduleId).portMappings.filter { port -> request.newModuleConfiguration.ports.contains(port.internalPort) })
            .applications(request.newModuleConfiguration.applications)
            .preparedConfigurations(request.newModuleConfiguration.preparedConfigurations)
            .manualConfigurations(request.newModuleConfiguration.manualConfigurations)
            .additionalFiles(request.newModuleConfiguration.additionalFiles)
            .build()
}
