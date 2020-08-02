package ru.art.platform.agent.specification

import ru.art.platform.agent.service.AgentAssemblyService.buildProject
import ru.art.platform.api.constants.ApIConstants.BUILD_PROJECT
import ru.art.platform.api.mapping.assembly.AssemblyEventMapper.fromAssemblyEvent
import ru.art.platform.api.mapping.request.AgentProjectBuildRequestMapper.toAgentProjectBuildRequest
import ru.art.reactive.service.constants.ReactiveServiceModuleConstants.ReactiveMethodProcessingMode.REACTIVE
import ru.art.rsocket.function.RsocketServiceFunction.rsocket

fun registerAssemblyService() {
    rsocket(BUILD_PROJECT)
            .requestMapper(toAgentProjectBuildRequest)
            .responseProcessingMode(REACTIVE)
            .responseMapper(fromAssemblyEvent)
            .handle(::buildProject)
}
