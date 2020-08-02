package ru.art.platform.specification

import ru.art.entity.CollectionMapping.stringCollectionMapper
import ru.art.entity.PrimitiveMapping.stringMapper
import ru.art.platform.constants.ServiceConstants.GET_UI_ACTIONS
import ru.art.platform.constants.ServiceConstants.GET_VERSION
import ru.art.platform.service.ManagementService
import ru.art.rsocket.function.RsocketServiceFunction.rsocket

fun registerManagementService() {
    rsocket(GET_VERSION)
            .responseMapper(stringMapper.fromModel)
            .produce(ManagementService::getVersion)
    rsocket(GET_UI_ACTIONS)
            .responseMapper(stringCollectionMapper.fromModel)
            .produce(ManagementService::getUiActions)
}