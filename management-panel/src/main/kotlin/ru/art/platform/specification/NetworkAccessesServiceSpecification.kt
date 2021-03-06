package ru.art.platform.specification

import ru.art.entity.PrimitiveMapping.*
import ru.art.platform.api.mapping.network.NetworkAccessRequestMapper.*
import ru.art.platform.constants.ServiceConstants.CHECK_NETWORK_ACCESS
import ru.art.platform.service.*
import ru.art.reactive.service.constants.ReactiveServiceModuleConstants
import ru.art.reactive.service.constants.ReactiveServiceModuleConstants.ReactiveMethodProcessingMode.REACTIVE
import ru.art.rsocket.function.RsocketServiceFunction.*

fun registerNetworkAccessesService() {
    rsocket(CHECK_NETWORK_ACCESS)
            .requestMapper(toNetworkAccessRequest)
            .responseMapper(boolMapper.fromModel)
            .responseProcessingMode(REACTIVE)
            .handle(NetworkService::checkNetworkAccess)

}
