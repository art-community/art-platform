package ru.art.platform.panel.agent.module.manager

import ru.art.platform.api.model.project.*
import ru.art.platform.api.model.resource.*
import ru.art.platform.common.constants.ErrorCodes.*
import ru.art.platform.common.constants.Resources.*
import ru.art.platform.common.exception.*
import ru.art.platform.panel.repository.OpenShiftResourceRepository.getOpenShiftResource
import ru.art.rsocket.model.*

interface ModuleManager {
    fun startAgent(): RsocketCommunicationTargetConfiguration
}

fun createModuleManager(resourceId: ResourceIdentifier, project: Project) = when (resourceId.type) {
    OPEN_SHIFT_RESOURCE -> OpenShiftModuleManager(getOpenShiftResource(resourceId.id), project)
    else -> throw PlatformException(UNKNOWN_RESOURCE_TYPE, "Unknown resource type '${resourceId.type}")
}
