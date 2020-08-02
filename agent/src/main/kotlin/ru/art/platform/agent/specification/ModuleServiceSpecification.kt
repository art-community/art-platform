package ru.art.platform.agent.specification

import ru.art.platform.agent.service.AgentModuleService
import ru.art.platform.api.constants.ApIConstants.*
import ru.art.platform.api.mapping.module.ModuleMapper.fromModule
import ru.art.platform.api.mapping.request.AgentModuleDeleteRequestMapper.toAgentModuleDeleteRequest
import ru.art.platform.api.mapping.request.AgentModuleInstallRequestMapper.toAgentModuleInstallRequest
import ru.art.platform.api.mapping.request.AgentModuleRestartRequestMapper.toAgentModuleRestartRequest
import ru.art.platform.api.mapping.request.AgentModuleStopRequestMapper.toAgentModuleStopRequest
import ru.art.platform.api.mapping.request.AgentModuleUpdateRequestMapper.toAgentModuleUpdateRequest
import ru.art.reactive.service.constants.ReactiveServiceModuleConstants.ReactiveMethodProcessingMode.REACTIVE
import ru.art.rsocket.function.RsocketServiceFunction.rsocket


fun registerModuleService() {
    rsocket(INSTALL_MODULE)
            .requestMapper(toAgentModuleInstallRequest)
            .responseProcessingMode(REACTIVE)
            .responseMapper(fromModule)
            .handle(AgentModuleService::installModule)
    rsocket(UPDATE_MODULE)
            .requestMapper(toAgentModuleUpdateRequest)
            .responseProcessingMode(REACTIVE)
            .responseMapper(fromModule)
            .handle(AgentModuleService::updateModule)
    rsocket(STOP_MODULE)
            .requestMapper(toAgentModuleStopRequest)
            .responseProcessingMode(REACTIVE)
            .responseMapper(fromModule)
            .handle(AgentModuleService::stopModule)
    rsocket(DELETE_MODULE)
            .requestMapper(toAgentModuleDeleteRequest)
            .responseProcessingMode(REACTIVE)
            .responseMapper(fromModule)
            .handle(AgentModuleService::deleteModule)
    rsocket(RESTART_MODULE)
            .requestMapper(toAgentModuleRestartRequest)
            .responseProcessingMode(REACTIVE)
            .responseMapper(fromModule)
            .handle(AgentModuleService::restartModule)
}
