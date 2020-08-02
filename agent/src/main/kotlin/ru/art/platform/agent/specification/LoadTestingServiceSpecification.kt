package ru.art.platform.agent.specification

import ru.art.platform.agent.service.AgentLoadTestingService
import ru.art.platform.api.constants.ApIConstants.LOAD_PROJECT
import ru.art.platform.api.mapping.load.LoadTestEventMapper.fromLoadTestEvent
import ru.art.platform.api.mapping.request.AgentLoadTestRequestMapper.toAgentLoadTestRequest
import ru.art.reactive.service.constants.ReactiveServiceModuleConstants.ReactiveMethodProcessingMode.REACTIVE
import ru.art.rsocket.function.RsocketServiceFunction.rsocket

fun registerLoadTestingService() {
    rsocket(LOAD_PROJECT)
            .requestMapper(toAgentLoadTestRequest)
            .responseMapper(fromLoadTestEvent)
            .responseProcessingMode(REACTIVE)
            .handle(AgentLoadTestingService::startLoadTesting)
}
