package ru.art.platform.panel.specification

import ru.art.entity.CollectionMapping.*
import ru.art.entity.PrimitiveMapping.*
import ru.art.platform.api.mapping.log.LogMapper.*
import ru.art.platform.api.mapping.request.LogRecordRequestMapper.*
import ru.art.platform.common.broker.PlatformEventMapper.fromPlatformEvent
import ru.art.platform.panel.constants.ServiceConstants.ADD_LOG_RECORD
import ru.art.platform.panel.constants.ServiceConstants.DELETE_LOG
import ru.art.platform.panel.constants.ServiceConstants.GET_LOG
import ru.art.platform.panel.constants.ServiceConstants.GET_LOGS
import ru.art.platform.panel.constants.ServiceConstants.SUBSCRIBE_ON_LOG
import ru.art.platform.panel.constants.ServiceConstants.UPDATE_LOG
import ru.art.platform.panel.repository.*
import ru.art.platform.panel.service.*
import ru.art.reactive.service.constants.ReactiveServiceModuleConstants.ReactiveMethodProcessingMode.*
import ru.art.rsocket.function.RsocketServiceFunction.*

fun registerLogService() {
    rsocket(ADD_LOG_RECORD)
            .requestMapper(toLogRecordRequest)
            .responseMapper(fromLog)
            .handle(LogsService::addLogRecord)
    rsocket(UPDATE_LOG)
            .requestMapper(toLog)
            .responseMapper(fromLog)
            .handle(LogRepository::putLog)
    rsocket(DELETE_LOG)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromLog)
            .handle(LogRepository::deleteLog)
    rsocket(GET_LOG)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromLog)
            .handle(LogRepository::getLog)
    rsocket(GET_LOGS)
            .responseMapper(collectionValueFromModel(fromLog)::map)
            .produce(LogRepository::getLogs)
    rsocket(SUBSCRIBE_ON_LOG)
            .responseMapper(fromPlatformEvent)
            .responseProcessingMode(REACTIVE)
            .produce(LogsService::subscribeOnLog)
}
