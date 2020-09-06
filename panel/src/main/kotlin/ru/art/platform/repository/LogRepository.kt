package ru.art.platform.repository

import ru.art.platform.api.mapping.log.LogMapper.*
import ru.art.platform.api.model.log.*
import ru.art.platform.common.constants.*
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.common.exception.*
import ru.art.tarantool.dao.TarantoolDao.*
import java.util.*

object LogRepository {
    fun putLog(log: Log): Log = toLog.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(LOG_CAMEL_CASE, fromLog.map(log)))

    fun deleteLog(id: Long): Optional<Log> = tarantool(PLATFORM_CAMEL_CASE)
            .delete(LOG_CAMEL_CASE, setOf(id)).map(toLog::map)

    fun getLog(id: Long): Log = tarantool(PLATFORM_CAMEL_CASE)
            .get(LOG_CAMEL_CASE, setOf(id))
            .map(toLog::map)
            .orElseThrow { PlatformException(ErrorCodes.LOG_DOES_NOT_EXISTS, "Log with id $id does not exists") }

    fun getLogs(): List<Log> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(LOG_CAMEL_CASE)
            .map(toLog::map)


}