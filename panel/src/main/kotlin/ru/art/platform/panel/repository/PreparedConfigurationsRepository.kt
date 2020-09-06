package ru.art.platform.panel.repository

import ru.art.platform.api.mapping.configuration.PreparedConfigurationMapper.fromPreparedConfiguration
import ru.art.platform.api.mapping.configuration.PreparedConfigurationMapper.toPreparedConfiguration
import ru.art.platform.api.model.configuration.PreparedConfiguration
import ru.art.platform.common.constants.ErrorCodes.PREPARED_CONFIGURATION_DOES_NOT_EXISTS
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.panel.constants.DbConstants.PREPARED_CONFIGURATIONS_SPACE
import ru.art.platform.panel.constants.DbConstants.PREPARED_CONFIGURATION_INDEX_NAME
import ru.art.tarantool.dao.TarantoolDao.tarantool
import java.util.*

object PreparedConfigurationsRepository {
    fun putNewPreparedConfiguration(resource: PreparedConfiguration): PreparedConfiguration = toPreparedConfiguration.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(PREPARED_CONFIGURATIONS_SPACE, fromPreparedConfiguration.map(resource.toBuilder().id(null).build())))

    fun putPreparedConfiguration(resource: PreparedConfiguration): PreparedConfiguration = toPreparedConfiguration.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(PREPARED_CONFIGURATIONS_SPACE, fromPreparedConfiguration.map(resource)))

    fun getPreparedConfiguration(id: Long): PreparedConfiguration = tarantool(PLATFORM_CAMEL_CASE)
            .get(PREPARED_CONFIGURATIONS_SPACE, setOf(id))
            .map(toPreparedConfiguration::map)
            .orElseThrow { PlatformException(PREPARED_CONFIGURATION_DOES_NOT_EXISTS, "Prepared Configuration with an id '${id}' does not exist") }

    fun getPreparedConfiguration(projectId: Long, profile: String, name: String): Optional<PreparedConfiguration> = tarantool(PLATFORM_CAMEL_CASE)
            .getByIndex(PREPARED_CONFIGURATIONS_SPACE, PREPARED_CONFIGURATION_INDEX_NAME, setOf(projectId, profile, name))
            .map(toPreparedConfiguration::map)

    fun getPreparedConfigurations(projectId: Long, profile: String): Set<PreparedConfiguration> = tarantool(PLATFORM_CAMEL_CASE)
            .selectByIndex(PREPARED_CONFIGURATIONS_SPACE, PREPARED_CONFIGURATION_INDEX_NAME, setOf(projectId, profile))
            .map(toPreparedConfiguration::map)
            .toSet()

    fun deletePreparedConfiguration(id: Long): PreparedConfiguration = tarantool(PLATFORM_CAMEL_CASE)
            .delete(PREPARED_CONFIGURATIONS_SPACE, id)
            .map(toPreparedConfiguration::map)
            .orElseThrow { PlatformException(PREPARED_CONFIGURATION_DOES_NOT_EXISTS, "Prepared Configuration with an id '${id}' does not exist") }

    fun getPreparedConfigurations(): Set<PreparedConfiguration> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(PREPARED_CONFIGURATIONS_SPACE)
            .map(toPreparedConfiguration::map)
            .toSet()
}
