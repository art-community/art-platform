package ru.art.platform.agent.manager

import ru.art.core.context.Context.contextConfiguration
import ru.art.platform.agent.constants.ModuleImageConstants.MODULE_PATH
import ru.art.platform.agent.dsl.AgentDsl.installModule
import ru.art.platform.agent.dsl.AgentDsl.updateModule
import ru.art.platform.agent.dsl.deleteDeploymentOnOpenShift
import ru.art.platform.agent.dsl.restartDeploymentOnOpenShift
import ru.art.platform.agent.dsl.stopDeploymentOnOpenShift
import ru.art.platform.agent.extension.findOpenShiftResource
import ru.art.platform.api.model.external.PortMapping
import ru.art.platform.api.model.module.Module
import ru.art.platform.api.model.module.Module.ModuleBuilder
import ru.art.platform.api.model.request.*
import ru.art.platform.common.emitter.ReactiveEmitter

object AgentOpenShiftModuleManager {
    fun ReactiveEmitter<ModuleBuilder, Module>.installOnOpenShift(request: AgentModuleInstallRequest) {
        val resource = request.openShiftResources.findOpenShiftResource(current().resourceId)
        installModule(resource, current().externalId.id, current().artifact.externalId.id) {
            projectName(request.projectId.id)
            workingDirectory("$MODULE_PATH/${request.projectId.id}/${current().artifact.name}")
            replicationsCount(current().count)
            applications(request.applications)
            current().url?.takeIf { url -> !url.url.isNullOrBlank() }?.let { url ->
                port(url.port)
                publish(url.url, url.port)
            }
            if (!current().parameters.isNullOrBlank()) {
                containerArguments(current().parameters)
            }
            if (!current().ports.isNullOrEmpty()) {
                addPorts(current().ports.toSet())
            }
            if (!request.additionalFiles.isNullOrEmpty()) {
                binaryFiles(request.additionalFiles.map { file -> file.name to file.bytes }.toMap())
            }
            if (!request.configurationFiles.isNullOrEmpty()) {
                configs(request.configurationFiles.map { file -> file.name to file.content }.toMap())
            }

            install {
                emit {
                    if (!clusterIp.isNullOrBlank()) {
                        internalIp(clusterIp)
                    }
                    current().url?.let { url ->
                        if (!routeUrl.isNullOrBlank()) {
                            url(url.toBuilder().url(routeUrl).build())
                        }
                    }
                    clearPortMappings()
                    portMappings(nodePortMapping.map { entry ->
                        PortMapping.builder()
                                .internalPort(entry.key)
                                .externalPort(entry.value)
                                .build()
                    })
                }
            }
        }
    }

    fun ReactiveEmitter<ModuleBuilder, Module>.updateOnOpenShift(request: AgentModuleUpdateRequest) {
        val resource = request.openShiftResources.findOpenShiftResource(request.newModule.resourceId)
        updateModule(resource, current().name, current().artifact.externalId.id) {
            request.skipChangesCheck?.let(::skipChangesCheck)
            projectName(request.projectId.id)
            current().internalIp?.let(::internalIp)
            workingDirectory("$MODULE_PATH/${request.projectId.id}/${current().artifact.name}")
            replicationsCount(current().count)
            applications(request.applications)
            current().url?.takeIf { url -> !url.url.isNullOrBlank() }?.let { url ->
                val mappingBuilder = PortMapping.builder().internalPort(url.port)
                current().portMappings
                        .find { mapping -> mapping.internalPort == url.port }
                        ?.let { mapping -> mappingBuilder.externalPort(mapping.externalPort) }
                port(mappingBuilder.build())
                publish(url.url, url.port)
            }
            if (!current().parameters.isNullOrBlank()) {
                containerArguments(current().parameters)
            }
            if (!current().portMappings.isNullOrEmpty() || !current().ports.isNullOrEmpty()) {
                val external = current().portMappings?.toSet() ?: emptySet()
                val internal = current().ports?.toSet() ?: emptySet()
                addPorts(external + internal
                        .filter { port -> external.none { externalPort -> externalPort.internalPort == port } }
                        .map { port -> PortMapping.builder().internalPort(port).build() })
            }
            if (!request.additionalFiles.isNullOrEmpty()) {
                binaryFiles(request.additionalFiles.map { file -> file.name to file.bytes }.toMap())
            }
            if (!request.configurationFiles.isNullOrEmpty()) {
                configs(request.configurationFiles.map { file -> file.name to file.content }.toMap())
            }
            update {
                emit {
                    if (!clusterIp.isNullOrBlank()) {
                        internalIp(clusterIp)
                    }
                    current().url?.let { url ->
                        if (!routeUrl.isNullOrBlank()) {
                            url(url.toBuilder().url(routeUrl).build())
                        }
                    }
                    clearPortMappings()
                    portMappings(nodePortMapping.map { entry ->
                        PortMapping.builder()
                                .internalPort(entry.key)
                                .externalPort(entry.value)
                                .build()
                    })
                }
            }
            return@updateModule this
        }
    }

    fun ReactiveEmitter<ModuleBuilder, Module>.stopOnOpenShift(request: AgentModuleStopRequest) {
        val resource = request.openShiftResources.findOpenShiftResource(current().resourceId)
        stopDeploymentOnOpenShift(resource, request.projectId.id, current().externalId.id)
    }

    fun ReactiveEmitter<ModuleBuilder, Module>.restartOnOpenShift(request: AgentModuleRestartRequest) {
        val resource = request.openShiftResources.findOpenShiftResource(current().resourceId)
        restartDeploymentOnOpenShift(resource, request.projectId.id, current().externalId.id, current().count)
    }

    fun ReactiveEmitter<ModuleBuilder, Module>.deleteOnOpenShift(request: AgentModuleDeleteRequest) {
        val resource = request.openShiftResources.findOpenShiftResource(current().resourceId)
        deleteDeploymentOnOpenShift(resource, request.projectId.id, current().externalId.id)
    }
}
