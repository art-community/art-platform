package ru.art.platform.agent.initialzier

import ru.art.config.extensions.ConfigExtensions.configString
import ru.art.core.extension.ExceptionExtensions.ifExceptionOrEmpty
import ru.art.platform.api.model.project.Project
import ru.art.platform.api.model.resource.OpenShiftResource
import ru.art.platform.common.extractor.extractHostName
import ru.art.platform.configurator.OpenShiftAgentConfigurator.configure
import ru.art.platform.constants.ConfigKeys.AGENT_PROJECT_INITIALIZER_PROJECT_NAME_KEY
import ru.art.platform.constants.OpenShiftConstants.PLATFORM_PROJECT_INITIALIZER
import ru.art.platform.open.shift.manager.startOpenShiftAgent
import ru.art.platform.open.shift.service.createProject
import ru.art.platform.open.shift.service.getProject
import ru.art.platform.open.shift.service.openShift
import ru.art.rsocket.model.RsocketCommunicationTargetConfiguration
import ru.art.rsocket.model.RsocketCommunicationTargetConfiguration.rsocketCommunicationTarget

class OpenShiftProjectInitializer(private val resource: OpenShiftResource, private val project: Project) : ProjectInitializer {
    override fun startAgent(): RsocketCommunicationTargetConfiguration {
        openShift(resource) {
            getProject(project.externalId.id) ?: createProject(project.externalId.id)
        }
        val agent = startOpenShiftAgent(resource, "$PLATFORM_PROJECT_INITIALIZER-${project.id}") {
            configure()
            val initializerProject = ifExceptionOrEmpty({ configString(AGENT_PROJECT_INITIALIZER_PROJECT_NAME_KEY) }, project.externalId.id)
            projectName(initializerProject)
            project.openShiftConfiguration?.platformPodsNodeSelector?.forEach { label -> nodeSelector(label.name, label.value) }
            reusable()
        }
        return rsocketCommunicationTarget()
                .host(extractHostName(resource.apiUrl))
                .tcpPort(agent.rsocket)
                .build()
    }
}
