package ru.art.platform.agent.loader

import ru.art.platform.api.model.load.*
import ru.art.platform.api.model.project.*
import ru.art.platform.api.model.resource.*
import ru.art.platform.common.constants.ErrorCodes.*
import ru.art.platform.common.constants.Resources.*
import ru.art.platform.common.exception.*
import ru.art.platform.repository.OpenShiftResourceRepository.getOpenShiftResource
import ru.art.rsocket.model.*

interface ProjectLoadTestRunner {
    fun startAgent(): RsocketCommunicationTargetConfiguration

    fun stopAgent()
}

fun createProjectLoadTestRunner(resourceId: ResourceIdentifier, loadTest: LoadTest, project: Project) = when (resourceId.type) {
    OPEN_SHIFT_RESOURCE -> OpenShiftProjectLoadTestRunner(getOpenShiftResource(resourceId.id), loadTest, project)
    else -> throw PlatformException(UNKNOWN_RESOURCE_TYPE, "Unknown resource type '${resourceId.type}")
}