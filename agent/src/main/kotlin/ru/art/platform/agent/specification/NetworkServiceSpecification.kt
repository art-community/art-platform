package ru.art.platform.agent.specification

import ru.art.entity.PrimitiveMapping.boolMapper
import ru.art.platform.agent.service.AgentNetworkService
import ru.art.platform.api.constants.ApIConstants.CHECK_NETWORK_ACCESS
import ru.art.platform.api.mapping.network.NetworkAccessRequestMapper.toNetworkAccessRequest
import ru.art.rsocket.function.RsocketServiceFunction.rsocket

fun registerNetworkService() {
    rsocket(CHECK_NETWORK_ACCESS)
            .requestMapper(toNetworkAccessRequest)
            .responseMapper(boolMapper.fromModel)
            .handle(AgentNetworkService::checkNetworkAccess)

}
