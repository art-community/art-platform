package ru.art.platform.panel.agent.module.manager

import ru.art.config.extensions.ConfigExtensions.configString
import ru.art.core.extension.ExceptionExtensions.ifExceptionOrEmpty
import ru.art.platform.api.model.project.Project
import ru.art.platform.api.model.resource.OpenShiftResource
import ru.art.platform.common.extractor.extractHostName
import ru.art.platform.panel.configurator.OpenShiftAgentConfigurator.configure
import ru.art.platform.panel.constants.ConfigKeys.AGENT_MODULE_MANAGER_PROJECT_NAME_KEY
import ru.art.platform.panel.constants.OpenShiftConstants.PLATFORM_MODULE_MANAGER
import ru.art.platform.open.shift.manager.startOpenShiftAgent
import ru.art.rsocket.model.RsocketCommunicationTargetConfiguration
import ru.art.rsocket.model.RsocketCommunicationTargetConfiguration.rsocketCommunicationTarget

class OpenShiftModuleManager(private val resource: OpenShiftResource, private val project: Project) : ModuleManager {
    override fun startAgent(): RsocketCommunicationTargetConfiguration = rsocketCommunicationTarget()
            .host(extractHostName(resource.apiUrl))
            .tcpPort(startOpenShiftAgent(resource, "$PLATFORM_MODULE_MANAGER-${project.id}") {
                configure()
                val managerProject = ifExceptionOrEmpty({ configString(AGENT_MODULE_MANAGER_PROJECT_NAME_KEY) }, project.externalId.id)
                project.openShiftConfiguration?.platformPodsNodeSelector?.forEach { label -> nodeSelector(label.name, label.value) }
                projectName(managerProject)
                reusable()
            }.rsocket)
            .build()
}
