package ru.art.platform.agent.specification

import ru.art.platform.agent.service.AgentProjectChangesService
import ru.art.platform.agent.service.AgentProjectInitializationService
import ru.art.platform.api.constants.ApIConstants.COMPUTE_PROJECT_CHANGES
import ru.art.platform.api.constants.ApIConstants.INITIALIZE_PROJECT
import ru.art.platform.api.mapping.project.ProjectChangesMapper.fromProjectChanges
import ru.art.platform.api.mapping.project.ProjectEventMapper.fromProjectEvent
import ru.art.platform.api.mapping.request.AgentProjectInitializationRequestMapper.toAgentProjectInitializationRequest
import ru.art.platform.api.mapping.request.ProjectChangesRequestMapper.toProjectChangesRequest
import ru.art.reactive.service.constants.ReactiveServiceModuleConstants.ReactiveMethodProcessingMode.REACTIVE
import ru.art.rsocket.function.RsocketServiceFunction.rsocket

fun registerProjectService() {
    rsocket(INITIALIZE_PROJECT)
            .requestMapper(toAgentProjectInitializationRequest)
            .responseProcessingMode(REACTIVE)
            .responseMapper(fromProjectEvent)
            .handle(AgentProjectInitializationService::initializeProject)
    rsocket(COMPUTE_PROJECT_CHANGES)
            .requestMapper(toProjectChangesRequest)
            .responseMapper(fromProjectChanges)
            .handle(AgentProjectChangesService::computeProjectChanges)
}
