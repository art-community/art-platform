package ru.art.platform.repository

import ru.art.platform.api.mapping.assembly.AssemblyConfigurationMapper.*
import ru.art.platform.api.model.assembly.*
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.constants.DbConstants.ASSEMBLY_CONFIGURATION_SPACE
import ru.art.tarantool.dao.TarantoolDao.*

object AssemblyConfigurationRepository {
    fun getAssemblyConfiguration(projectId: Long): AssemblyConfiguration = tarantool(PLATFORM_CAMEL_CASE)
            .get(ASSEMBLY_CONFIGURATION_SPACE, projectId)
            .map(toAssemblyConfiguration::map)
            .orElse(AssemblyConfiguration.builder().id(projectId).build())

    fun saveAssemblyConfiguration(configuration: AssemblyConfiguration): AssemblyConfiguration = toAssemblyConfiguration.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(ASSEMBLY_CONFIGURATION_SPACE, fromAssemblyConfiguration.map(configuration)))
}