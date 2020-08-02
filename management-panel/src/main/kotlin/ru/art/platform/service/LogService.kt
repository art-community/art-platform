package ru.art.platform.service

import reactor.core.publisher.*
import ru.art.platform.api.model.log.*
import ru.art.platform.api.model.request.*
import ru.art.platform.broker.*
import ru.art.platform.common.broker.*
import ru.art.platform.repository.LogRepository.getLog
import ru.art.platform.repository.LogRepository.putLog

object LogsService {
    fun addLogRecord(request: LogRecordRequest): Log = putLog(getLog(request.logId)
            .toBuilder()
            .record(request.record)
            .build())
            .updated()

    fun subscribeOnLog(): Flux<PlatformEvent> = logConsumer()
}