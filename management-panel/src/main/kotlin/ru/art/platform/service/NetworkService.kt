package ru.art.platform.service

import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.create
import ru.art.platform.agent.network.checker.openShiftNetworkChecker
import ru.art.platform.api.model.network.NetworkAccessRequest
import ru.art.platform.client.connectToAgent
import ru.art.platform.common.constants.ErrorCodes.UNKNOWN_RESOURCE_TYPE
import ru.art.platform.common.constants.Resources.OPEN_SHIFT_RESOURCE
import ru.art.platform.common.exception.PlatformException
import ru.art.task.deferred.executor.SchedulerModuleActions.asynchronous

object NetworkService {
    fun checkNetworkAccess(request: NetworkAccessRequest): Mono<Boolean> = create<Boolean> { sink ->
        asynchronous {
            connectToAgent(when (request.resourceId.type) {
                OPEN_SHIFT_RESOURCE -> openShiftNetworkChecker(request.resourceId, request.openShiftPodConfiguration).startAgent()
                else -> throw PlatformException(UNKNOWN_RESOURCE_TYPE, "Unknown resource type '${request.resourceId.type}")
            }).checkNetworkAccess(request).subscribe(sink::success)
        }
    }
}
