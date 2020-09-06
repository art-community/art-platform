package ru.art.platform.service

import reactor.core.publisher.Flux
import ru.art.platform.agent.module.manager.createModuleManager
import ru.art.platform.api.model.module.Module
import ru.art.platform.api.model.module.ModuleFilterCriteria
import ru.art.platform.api.model.module.ModuleInformation
import ru.art.platform.api.model.request.AssembledProjectArtifactsRequest
import ru.art.platform.api.model.request.ModuleInstallationRequest
import ru.art.platform.api.model.request.ModuleUpdateRequest
import ru.art.platform.api.model.request.UpdateModulesVersionRequest
import ru.art.platform.broker.added
import ru.art.platform.broker.deleted
import ru.art.platform.broker.moduleConsumer
import ru.art.platform.broker.updated
import ru.art.platform.common.broker.PlatformEvent
import ru.art.platform.common.constants.ErrorCodes.MODULE_ALREADY_EXISTS
import ru.art.platform.common.constants.States.*
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.factory.ModuleFactory.createModule
import ru.art.platform.filter.ModuleFilter
import ru.art.platform.repository.ModuleRepository
import ru.art.platform.repository.ModuleRepository.getModule
import ru.art.platform.repository.ModuleRepository.getModules
import ru.art.platform.repository.ModuleRepository.getProjectModules
import ru.art.platform.repository.ModuleRepository.putModule
import ru.art.platform.repository.ProjectRepository.getProject
import ru.art.platform.service.ModuleManagementService.deleteModule
import ru.art.platform.service.ModuleManagementService.installModule
import ru.art.platform.service.ModuleManagementService.restartModule
import ru.art.platform.service.ModuleManagementService.stopModule
import ru.art.platform.service.ModuleManagementService.updateModule
import ru.art.platform.service.ProjectService.getAssembledProjectArtifacts

object ModuleService {
    fun startModuleInstallation(request: ModuleInstallationRequest) = getProjectModules(request.projectId)
            .find { module -> module.name == request.configuration.name }
            ?.let { throw PlatformException(MODULE_ALREADY_EXISTS) }
            ?: putModule(createModule(request))

    fun processModuleInstallation(module: Module) {
        val newModule = putModule(module.toBuilder().state(MODULE_INSTALLATION_STARTED_STATE).build()).added()
        installModule(newModule, createModuleManager(module.resourceId, getProject(module.projectId)))
    }

    fun reinstallModule(id: Long) {
        val module = getModule(id)
        val newModule = putModule(module.toBuilder().state(MODULE_INSTALLATION_STARTED_STATE).build()).updated()
        installModule(newModule, createModuleManager(module.resourceId, getProject(module.projectId)))
    }

    fun startModuleUpdating(request: ModuleUpdateRequest): Module = putModule(createModule(getModule(request.moduleId), request))

    fun processModuleUpdating(module: Module) {
        val newModule = putModule(module.toBuilder().state(MODULE_UPDATE_STARTED_STATE).build()).updated()
        updateModule(newModule, createModuleManager(module.resourceId, getProject(module.projectId)))
    }

    fun updateModulesVersion(request: UpdateModulesVersionRequest) {
        val assembledProjectArtifacts = getAssembledProjectArtifacts(AssembledProjectArtifactsRequest.builder()
                .projectId(getModule(request.ids.first()).projectId)
                .version(request.version)
                .build())
        request.ids
                .map(::getModule)
                .filter { module -> module.state == MODULE_RUN_STATE }
                .map { module ->
                    putModule(module.toBuilder()
                            .artifact(module.artifact
                                    .toBuilder()
                                    .externalId(assembledProjectArtifacts
                                            .first { artifact ->
                                                artifact.name == module.artifact.name && artifact.externalId.resourceId == module.artifact.externalId.resourceId
                                            }.externalId)
                                    .version(request.version)
                                    .build())
                            .build())
                }
                .forEach(::processModuleUpdating)
    }

    fun refreshModuleArtifact(id: Long) {
        val newModule = putModule(getModule(id).toBuilder().state(MODULE_UPDATE_STARTED_STATE).build()).updated()
        updateModule(newModule, createModuleManager(newModule.resourceId, getProject(newModule.projectId)), true)
    }

    fun stopModule(id: Long) {
        val module = putModule(getModule(id).toBuilder().state(MODULE_STOP_STARTED_STATE).build()).updated()
        stopModule(module, createModuleManager(module.resourceId, getProject(module.projectId)))
    }

    fun restartModule(id: Long) {
        val module = putModule(getModule(id).toBuilder().state(MODULE_RESTART_STARTED_STATE).build()).updated()
        restartModule(module, createModuleManager(module.resourceId, getProject(module.projectId)))
    }

    fun deleteModuleFromResource(id: Long): Module {
        val module = putModule(getModule(id).toBuilder().state(MODULE_UNINSTALL_STARTED_STATE).build()).updated()
        deleteModule(module, createModuleManager(module.resourceId, getProject(module.projectId)))
        return module
    }

    fun deleteModule(id: Long) = ModuleRepository.deleteModule(id).deleted()

    fun subscribeOnModule(): Flux<PlatformEvent> = moduleConsumer()

    fun deleteProjectModules(projectId: Long) = getProjectModules(projectId).forEach { module -> deleteModule(module.id) }

    fun failChangingModules() = getModules()
            .filter { module -> module.isChanging }
            .forEach { module -> putModule(module.toBuilder().state(MODULE_INVALID_STATE).build()) }

    fun getFilteredModules(criteria: ModuleFilterCriteria): List<ModuleInformation> = ModuleFilter(criteria).filter()
}
