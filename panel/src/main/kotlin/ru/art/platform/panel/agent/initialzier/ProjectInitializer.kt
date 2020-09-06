package ru.art.platform.panel.agent.initialzier

import ru.art.platform.api.model.project.Project
import ru.art.platform.api.model.resource.ResourceIdentifier
import ru.art.platform.common.constants.ErrorCodes.UNKNOWN_RESOURCE_TYPE
import ru.art.platform.common.constants.Resources.OPEN_SHIFT_RESOURCE
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.panel.repository.OpenShiftResourceRepository.getOpenShiftResource
import ru.art.rsocket.model.RsocketCommunicationTargetConfiguration

interface ProjectInitializer {
    fun startAgent(): RsocketCommunicationTargetConfiguration
}

fun createProjectInitializer(resourceId: ResourceIdentifier, project: Project) = when (resourceId.type) {
    OPEN_SHIFT_RESOURCE -> OpenShiftProjectInitializer(getOpenShiftResource(resourceId.id), project)
    else -> throw PlatformException(UNKNOWN_RESOURCE_TYPE, "Unknown resource type '${resourceId.type}")
}
