package ru.art.platform.agent.network.checker

import ru.art.platform.api.model.openShift.OpenShiftPodConfiguration
import ru.art.platform.api.model.resource.ResourceIdentifier
import ru.art.platform.repository.OpenShiftResourceRepository.getOpenShiftResource
import ru.art.rsocket.model.RsocketCommunicationTargetConfiguration

interface NetworkChecker {
    fun startAgent(): RsocketCommunicationTargetConfiguration
}

fun openShiftNetworkChecker(resourceId: ResourceIdentifier, configuration: OpenShiftPodConfiguration?) =
        OpenShiftNetworkChecker(getOpenShiftResource(resourceId.id), configuration)
