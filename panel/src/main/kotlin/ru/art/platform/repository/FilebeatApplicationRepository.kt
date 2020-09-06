package ru.art.platform.repository

import ru.art.platform.api.mapping.filebeat.FilebeatApplicationMapper.fromFilebeatApplication
import ru.art.platform.api.mapping.filebeat.FilebeatApplicationMapper.toFilebeatApplication
import ru.art.platform.api.model.filebeat.FilebeatApplication
import ru.art.platform.common.constants.ErrorCodes.APPLICATION_DOES_NOT_EXISTS
import ru.art.platform.common.constants.PlatformKeywords.NAME_CAMEL_CASE
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.constants.DbConstants.FILEBEAT_APPLICATION_SPACE
import ru.art.tarantool.dao.TarantoolDao.tarantool
import java.util.*

object FilebeatApplicationRepository {
    fun getFilebeatApplications(): Set<FilebeatApplication> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(FILEBEAT_APPLICATION_SPACE)
            .map(toFilebeatApplication::map)
            .toSet()

    fun putNewFilebeatApplication(application: FilebeatApplication): FilebeatApplication =
            toFilebeatApplication.map(tarantool(PLATFORM_CAMEL_CASE).put(FILEBEAT_APPLICATION_SPACE, fromFilebeatApplication.map(application.toBuilder().id(null).build())))

    fun putFilebeatApplication(application: FilebeatApplication): FilebeatApplication =
            toFilebeatApplication.map(tarantool(PLATFORM_CAMEL_CASE).put(FILEBEAT_APPLICATION_SPACE, fromFilebeatApplication.map(application)))

    fun getFilebeatApplication(id: Long): FilebeatApplication = tarantool(PLATFORM_CAMEL_CASE)
            .get(FILEBEAT_APPLICATION_SPACE, setOf(id))
            .map(toFilebeatApplication::map)
            .orElseThrow { PlatformException(APPLICATION_DOES_NOT_EXISTS, "Filebeat application with id '${id}' does not exists") }

    fun getFilebeatApplication(name: String): Optional<FilebeatApplication> = tarantool(PLATFORM_CAMEL_CASE)
            .getByIndex(FILEBEAT_APPLICATION_SPACE, NAME_CAMEL_CASE, setOf(name))
            .map(toFilebeatApplication::map)

    fun deleteFilebeatApplication(id: Long): FilebeatApplication = tarantool(PLATFORM_CAMEL_CASE)
            .delete(FILEBEAT_APPLICATION_SPACE, id)
            .map(toFilebeatApplication::map)
            .orElseThrow { PlatformException(APPLICATION_DOES_NOT_EXISTS, "Filebeat application with id '${id}' does not exists") }
}
