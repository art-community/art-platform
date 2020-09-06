package ru.art.platform.panel.broker

import reactor.core.publisher.Flux
import ru.art.platform.api.mapping.log.LogMapper.fromLog
import ru.art.platform.api.model.log.Log
import ru.art.platform.common.broker.PlatformEvent
import ru.art.platform.common.broker.ReactiveBroker
import ru.art.platform.common.constants.EventTypes.*

private val logBroker = ReactiveBroker()

fun logAdded(log: Log) = logBroker.emit(PlatformEvent(ADD_EVENT, fromLog.map(log)))

fun logUpdated(log: Log) = logBroker.emit(PlatformEvent(UPDATE_EVENT, fromLog.map(log)))

fun logDeleted(log: Log) = logBroker.emit(PlatformEvent(DELETE_EVENT, fromLog.map(log)))

fun Log.updated(): Log {
    logUpdated(this)
    return this
}

fun Log.added(): Log {
    logAdded(this)
    return this
}

fun Log.deleted(): Log {
    logDeleted(this)
    return this
}

fun logConsumer(): Flux<PlatformEvent> = logBroker.consumer()
