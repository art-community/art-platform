package ru.art.platform.agent.builder

import ru.art.platform.api.model.assembly.*
import ru.art.platform.api.model.project.*
import ru.art.platform.api.model.resource.*
import ru.art.platform.common.constants.ErrorCodes.*
import ru.art.platform.common.constants.Resources.*
import ru.art.platform.common.exception.*
import ru.art.platform.repository.OpenShiftResourceRepository.getOpenShiftResource
import ru.art.rsocket.model.*

interface ProjectBuilder {
    fun startAgent(): RsocketCommunicationTargetConfiguration

    fun startCacheAgent(configuration: AssemblyConfiguration): RsocketCommunicationTargetConfiguration

    fun stopAgent()
}

fun createProjectBuilder(resourceId: ResourceIdentifier, assembly: Assembly, project: Project) = when (resourceId.type) {
    OPEN_SHIFT_RESOURCE -> OpenShiftProjectBuilder(getOpenShiftResource(resourceId.id), assembly, project)
    else -> throw PlatformException(UNKNOWN_RESOURCE_TYPE, "Unknown resource type '${resourceId.type}")
}