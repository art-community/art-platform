package ru.art.platform.panel.service

import ru.art.config.Config
import ru.art.config.ConfigProvider.config
import ru.art.config.constants.ConfigType.YAML
import ru.art.config.extensions.ConfigExtensions.configString
import ru.art.config.extensions.ConfigExtensions.configStringList
import ru.art.core.extension.ExceptionExtensions.ifExceptionOrEmpty
import ru.art.logging.LoggingModule.loggingModule
import ru.art.platform.api.model.resource.PlatformResource
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.common.constants.Resources.OPEN_SHIFT_RESOURCE
import ru.art.platform.panel.constants.CommonConstants.VERSION_SYSTEM_ENV
import ru.art.platform.panel.constants.ConfigKeys.AGENT_MODULE_MANAGER_PROJECT_NAME_KEY
import ru.art.platform.panel.constants.ConfigKeys.AGENT_PROJECT_INITIALIZER_PROJECT_NAME_KEY
import ru.art.platform.panel.constants.ConfigKeys.ID_KEY
import ru.art.platform.panel.constants.ConfigKeys.NAME_KEY
import ru.art.platform.panel.constants.ConfigKeys.PASSWORD_KEY
import ru.art.platform.panel.constants.ConfigKeys.RESOURCES_SECTION
import ru.art.platform.panel.constants.ConfigKeys.USER_DEFAULT_ACTIONS_KEY
import ru.art.platform.panel.constants.ConfigKeys.URL_KEY
import ru.art.platform.panel.constants.ConfigKeys.USER_NAME_KEY
import ru.art.platform.panel.constants.OpenShiftConstants.PLATFORM_ASSEMBLY_GRADLE_CACHE
import ru.art.platform.panel.constants.OpenShiftConstants.PLATFORM_MODULE_MANAGER
import ru.art.platform.panel.constants.OpenShiftConstants.PLATFORM_NETWORK_ACCESS_CHECKER
import ru.art.platform.panel.constants.OpenShiftConstants.PLATFORM_PROJECT_INITIALIZER
import ru.art.platform.open.shift.manager.stopOpenShiftAgent
import ru.art.platform.panel.repository.OpenShiftResourceRepository.getOpenShiftResource
import ru.art.platform.panel.repository.OpenShiftResourceRepository.getOpenShiftResources
import ru.art.platform.panel.repository.PlatformResourceRepository
import ru.art.platform.panel.repository.ProjectRepository.getProjects
import ru.art.platform.panel.service.AssemblyService.stopRunningAssemblies
import ru.art.platform.panel.service.ModuleService.failChangingModules
import ru.art.platform.panel.service.ProjectService.failStuckProjects
import java.lang.System.getenv

object ManagementService {
    fun getVersion(): String = getenv(VERSION_SYSTEM_ENV) ?: "development"

    fun getUiActions(): Set<String> = configStringList(USER_DEFAULT_ACTIONS_KEY).toSet()

    fun handleRestart() {
        try {
            failStuckProjects()
            stopRunningAssemblies()
            failChangingModules()
            stopRunningAgents()
        } catch (throwable: Throwable) {
            loggingModule().getLogger(ManagementService::class.java).warn(throwable.message, throwable)
        }
    }

    fun loadPlatformResources() {
        config(PLATFORM_CAMEL_CASE)
                .asYamlConfig()
                .at("/$RESOURCES_SECTION")
                .map { config -> Config(config, YAML) }
                .forEach { config ->
                    val id = config.getLong(ID_KEY)
                    val name = config.getString(NAME_KEY)
                    val url = config.getString(URL_KEY)
                    val userName = config.getString(USER_NAME_KEY)
                    val password = config.getString(PASSWORD_KEY)
                    PlatformResourceRepository.putPlatformResource(PlatformResource.builder()
                            .id(id)
                            .name(name)
                            .url(url)
                            .userName(userName)
                            .password(password)
                            .build())
                }
    }

    private fun stopRunningAgents() {
        getOpenShiftResources().parallelStream().forEach { resource ->
            stopOpenShiftAgent(resource, PLATFORM_NETWORK_ACCESS_CHECKER, PLATFORM_CAMEL_CASE)
        }
        getProjects().parallelStream().forEach { project ->
            when (project.initializationResourceId.type) {
                OPEN_SHIFT_RESOURCE -> {
                    val resource = getOpenShiftResource(project.initializationResourceId.id)
                    val moduleManagerProject = ifExceptionOrEmpty({ configString(AGENT_MODULE_MANAGER_PROJECT_NAME_KEY) }, project.externalId.id)
                    val projectInitializerProject = ifExceptionOrEmpty({ configString(AGENT_PROJECT_INITIALIZER_PROJECT_NAME_KEY) }, project.externalId.id)
                    stopOpenShiftAgent(resource, "$PLATFORM_PROJECT_INITIALIZER-${project.id}", projectInitializerProject)
                    stopOpenShiftAgent(resource, "$PLATFORM_MODULE_MANAGER-${project.id}", moduleManagerProject)
                    stopOpenShiftAgent(resource, PLATFORM_ASSEMBLY_GRADLE_CACHE, project.externalId.id)
                }
            }
        }
    }
}
