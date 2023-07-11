package ru.art.platform.agent.builder

import ru.art.config.extensions.ConfigExtensions.configString
import ru.art.core.extension.ExceptionExtensions.ifExceptionOrEmpty
import ru.art.platform.api.model.assembly.Assembly
import ru.art.platform.api.model.assembly.AssemblyConfiguration
import ru.art.platform.api.model.project.Project
import ru.art.platform.api.model.resource.OpenShiftResource
import ru.art.platform.common.constants.CommonConstants.*
import ru.art.platform.common.extractor.extractHostName
import ru.art.platform.configurator.OpenShiftAgentConfigurator.configure
import ru.art.platform.constants.ConfigKeys.AGENT_BUILDER_PROJECT_NAME_KEY
import ru.art.platform.constants.OpenShiftConstants.PLATFORM_ASSEMBLY_GRADLE_CACHE
import ru.art.platform.constants.OpenShiftConstants.PLATFORM_PROJECT_BUILDER
import ru.art.platform.open.shift.manager.startOpenShiftAgent
import ru.art.platform.open.shift.manager.stopOpenShiftAgent
import ru.art.rsocket.model.RsocketCommunicationTargetConfiguration
import ru.art.rsocket.model.RsocketCommunicationTargetConfiguration.rsocketCommunicationTarget

class OpenShiftProjectBuilder(private val resource: OpenShiftResource, private val assembly: Assembly, private val project: Project) : ProjectBuilder {
    override fun startAgent(): RsocketCommunicationTargetConfiguration = rsocketCommunicationTarget()
            .host(extractHostName(resource.apiUrl))
            .tcpPort(startOpenShiftAgent(resource, "$PLATFORM_PROJECT_BUILDER-${assembly.id}") {
                configure()
                allowPushingTo(project.externalId.id)
                val builderProject = ifExceptionOrEmpty({ configString(AGENT_BUILDER_PROJECT_NAME_KEY) }, project.externalId.id)
                project.openShiftConfiguration?.platformPodsNodeSelector?.forEach { label -> nodeSelector(label.name, label.value) }
                projectName(builderProject)
            }.rsocket)
            .build()

    override fun startCacheAgent(configuration: AssemblyConfiguration): RsocketCommunicationTargetConfiguration =
            rsocketCommunicationTarget()
                    .host(PLATFORM_ASSEMBLY_GRADLE_CACHE)
                    .tcpPort(startOpenShiftAgent(resource, PLATFORM_ASSEMBLY_GRADLE_CACHE) {
                        configure()
                        project.openShiftConfiguration?.platformPodsNodeSelector?.forEach { label -> nodeSelector(label.name, label.value) }
                        projectName(project.externalId.id)
                        customPort(CACHE_PORT_NAME, GRADLE_CACHE_PORT_PROPERTY)
                        environmentVariable(GRADLE_CACHE_PROPERTY, true.toString())
                        reusable()
                    }.custom.getValue(CACHE_PORT_NAME).service)
                    .build()


    override fun stopAgent() {
        stopOpenShiftAgent(resource, "$PLATFORM_PROJECT_BUILDER-${assembly.id}", ifExceptionOrEmpty({ configString(AGENT_BUILDER_PROJECT_NAME_KEY) }, project.externalId.id))
    }
}
