package ru.art.platform.service

import ru.art.logging.LoggingModule.loggingModule
import ru.art.platform.agent.module.manager.ModuleManager
import ru.art.platform.api.model.file.StringFile
import ru.art.platform.api.model.module.Module
import ru.art.platform.api.model.request.*
import ru.art.platform.broker.updated
import ru.art.platform.client.connectToAgent
import ru.art.platform.common.constants.States.*
import ru.art.platform.configurator.ModuleApplicationConfigurator.configureModuleApplications
import ru.art.platform.repository.ModuleRepository.getModule
import ru.art.platform.repository.ModuleRepository.putModule
import ru.art.platform.repository.ModuleRepository.tryGetModule
import ru.art.platform.repository.OpenShiftResourceRepository.getOpenShiftResources
import ru.art.platform.repository.PreparedConfigurationsRepository.getPreparedConfiguration
import ru.art.platform.repository.ProjectRepository.getProject
import ru.art.platform.repository.ProxyResourceRepository.getProxyResources
import ru.art.platform.service.FileService.getFile
import ru.art.platform.state.SessionState.localUser
import ru.art.task.deferred.executor.SchedulerModuleActions.asynchronous
import java.time.Instant.now

object ModuleManagementService {
    fun installModule(module: Module, manager: ModuleManager) {
        val user = localUser.get().toBuilder().build()
        with(module) {
            asynchronous {
                val project = getProject(projectId)
                try {
                    val configurationFiles = manualConfigurations + preparedConfigurations
                            .map { id -> getPreparedConfiguration(id.id) }
                            .map { configuration -> StringFile.builder().name(configuration.name).content(configuration.configuration).build() }
                            .toSet()
                    connectToAgent(manager.startAgent())
                            .installModule(AgentModuleInstallRequest.builder()
                                    .module(module.toBuilder().state(MODULE_INSTALLING_STATE).build().apply { updateModule(this) })
                                    .projectId(project.externalId)
                                    .openShiftResources(getOpenShiftResources())
                                    .proxyResources(getProxyResources())
                                    .additionalFiles(additionalFiles.map { id -> getFile(id.id) })
                                    .configurationFiles(configurationFiles)
                                    .applications(configureModuleApplications(applications))
                                    .user(user)
                                    .notificationsConfiguration(project.notificationsConfiguration)
                                    .build())
                            .doOnError { error -> handleModuleError(MODULE_INSTALLING_STATE, error) }
                            .doOnComplete { completeModule(MODULE_INSTALLING_STATE, MODULE_RUN_STATE) }
                            .subscribe { module -> updateModule(MODULE_INSTALLING_STATE, module) }
                } catch (error: Throwable) {
                    handleModuleError(error)
                }
            }
        }
    }

    fun updateModule(module: Module, manager: ModuleManager, skipChangesCheck: Boolean = false) {
        val user = localUser.get().toBuilder().build()
        with(module) {
            asynchronous {
                val project = getProject(projectId)
                try {
                    val configurationFiles = manualConfigurations + preparedConfigurations
                            .map { id -> getPreparedConfiguration(id.id) }
                            .map { configuration -> StringFile.builder().name(configuration.name).content(configuration.configuration).build() }
                            .toSet()
                    connectToAgent(manager.startAgent())
                            .updateModule((AgentModuleUpdateRequest.builder()
                                    .skipChangesCheck(skipChangesCheck)
                                    .newModule(module.toBuilder().state(MODULE_UPDATING_STATE).build().apply { updateModule(this) })
                                    .projectId(project.externalId)
                                    .openShiftResources(getOpenShiftResources())
                                    .proxyResources(getProxyResources())
                                    .additionalFiles(additionalFiles.map { id -> getFile(id.id) })
                                    .configurationFiles(configurationFiles)
                                    .applications(configureModuleApplications(applications))
                                    .user(user)
                                    .notificationsConfiguration(project.notificationsConfiguration)
                                    .build()))
                            .doOnError { error -> handleModuleError(MODULE_UPDATING_STATE, error) }
                            .doOnComplete { completeModule(MODULE_UPDATING_STATE, MODULE_RUN_STATE) }
                            .subscribe { module -> updateModule(MODULE_UPDATING_STATE, module) }
                } catch (error: Throwable) {
                    handleModuleError(error)
                }
            }
        }
    }

    fun stopModule(module: Module, manager: ModuleManager) {
        val user = localUser.get().toBuilder().build()
        with(module) {
            asynchronous {
                val project = getProject(projectId)
                try {
                    connectToAgent(manager.startAgent())
                            .stopModule(AgentModuleStopRequest.builder()
                                    .module(toBuilder().state(MODULE_STOPPING_STATE).build().apply { updateModule(this) })
                                    .projectId(project.externalId)
                                    .openShiftResources(getOpenShiftResources())
                                    .proxyResources(getProxyResources())
                                    .user(user)
                                    .notificationsConfiguration(project.notificationsConfiguration)
                                    .build())
                            .doOnError { error -> handleModuleError(MODULE_STOPPING_STATE, error) }
                            .doOnComplete { completeModule(MODULE_STOPPING_STATE, MODULE_STOPPED_STATE) }
                            .subscribe { module -> updateModule(MODULE_STOPPING_STATE, module) }
                } catch (error: Throwable) {
                    handleModuleError(error)
                }
            }
        }
    }

    fun restartModule(module: Module, manager: ModuleManager) {
        val user = localUser.get().toBuilder().build()
        with(module) {
            asynchronous {
                val project = getProject(projectId)
                try {
                    connectToAgent(manager.startAgent())
                            .restartModule(AgentModuleRestartRequest.builder()
                                    .module(toBuilder().state(MODULE_RESTARTING_STATE).build().apply { updateModule(this) })
                                    .projectId(project.externalId)
                                    .openShiftResources(getOpenShiftResources())
                                    .proxyResources(getProxyResources())
                                    .user(user)
                                    .notificationsConfiguration(project.notificationsConfiguration)
                                    .build())
                            .doOnError { error -> handleModuleError(MODULE_RESTARTING_STATE, error) }
                            .doOnComplete { completeModule(MODULE_RESTARTING_STATE, MODULE_RUN_STATE) }
                            .subscribe { module -> updateModule(MODULE_RESTARTING_STATE, module) }
                } catch (error: Throwable) {
                    handleModuleError(error)
                }
            }
        }
    }

    fun deleteModule(module: Module, manager: ModuleManager) {
        val user = localUser.get().toBuilder().build()
        with(module) {
            asynchronous {
                val project = getProject(projectId)
                try {
                    connectToAgent(manager.startAgent())
                            .deleteModule(AgentModuleDeleteRequest.builder()
                                    .module(toBuilder().state(MODULE_UNINSTALLING_STATE).build().apply { updateModule(this) })
                                    .projectId(project.externalId)
                                    .openShiftResources(getOpenShiftResources())
                                    .proxyResources(getProxyResources())
                                    .user(user)
                                    .notificationsConfiguration(project.notificationsConfiguration)
                                    .build())
                            .doOnError { error -> handleModuleError(MODULE_UNINSTALLING_STATE, error) }
                            .doOnComplete { completeModule(MODULE_UNINSTALLING_STATE, MODULE_NOT_INSTALLED_STATE) }
                            .subscribe { module -> updateModule(MODULE_UNINSTALLING_STATE, module) }
                } catch (error: Throwable) {
                    handleModuleError(error)
                }
            }
        }
    }

    private fun updateModule(state: String, newModule: Module) {
        tryGetModule(newModule.id)
                .filter { module -> module.state == state }
                .ifPresent { putModule(newModule).updated() }
    }

    private fun updateModule(newModule: Module) {
        tryGetModule(newModule.id)
                .ifPresent { putModule(newModule).updated() }
    }

    private fun Module.handleModuleError(state: String, error: Throwable) {
        tryGetModule(id)
                .filter { module -> module.state == state }
                .ifPresent {
                    loggingModule().getLogger(ModuleManagementService::class.java).error(error.message, error)
                    updateModule(getModule(id).toBuilder().state(MODULE_INVALID_STATE).updateTimeStamp(now().epochSecond).build())
                }
    }

    private fun Module.handleModuleError(error: Throwable) {
        tryGetModule(id)
                .ifPresent {
                    loggingModule().getLogger(ModuleManagementService::class.java).error(error.message, error)
                    updateModule(getModule(id).toBuilder().state(MODULE_INVALID_STATE).updateTimeStamp(now().epochSecond).build())
                }
    }

    private fun Module.completeModule(currentState: String, finalState: String) {
        tryGetModule(id)
                .filter { module -> module.state == currentState }
                .ifPresent { module ->
                    updateModule(module.toBuilder().state(finalState).updateTimeStamp(now().epochSecond).build())
                }
    }
}
