package ru.art.platform.panel.agent.network.checker

import ru.art.config.extensions.ConfigExtensions.*
import ru.art.platform.panel.agent.initialzier.ProjectInitializer
import ru.art.platform.api.model.openShift.OpenShiftPodConfiguration
import ru.art.platform.api.model.resource.OpenShiftResource
import ru.art.platform.common.extractor.extractHostName
import ru.art.platform.panel.configurator.OpenShiftAgentConfigurator.configure
import ru.art.platform.panel.constants.ConfigKeys.OPEN_SHIFT_PLATFORM_PROJECT_NAME_KEY
import ru.art.platform.panel.constants.OpenShiftConstants.PLATFORM_NETWORK_ACCESS_CHECKER
import ru.art.platform.open.shift.manager.startOpenShiftAgent
import ru.art.rsocket.model.RsocketCommunicationTargetConfiguration
import ru.art.rsocket.model.RsocketCommunicationTargetConfiguration.rsocketCommunicationTarget

class OpenShiftNetworkChecker(private val resource: OpenShiftResource, private val configuration: OpenShiftPodConfiguration?) : NetworkChecker {
    override fun startAgent(): RsocketCommunicationTargetConfiguration = rsocketCommunicationTarget()
            .host(extractHostName(resource.apiUrl))
            .tcpPort(startOpenShiftAgent(resource, PLATFORM_NETWORK_ACCESS_CHECKER) {
                configuration?.nodeSelector?.let { selector -> selector.forEach { label -> nodeSelector(label.name, label.value) }}
                configure()
                projectName(configString(OPEN_SHIFT_PLATFORM_PROJECT_NAME_KEY))
            }.rsocket)
            .build()
}
