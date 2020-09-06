package ru.art.platform.panel.repository

import ru.art.platform.api.mapping.assembly.AssemblyInformationMapper.toAssemblyInformation
import ru.art.platform.api.mapping.assembly.AssemblyMapper.fromAssembly
import ru.art.platform.api.mapping.assembly.AssemblyMapper.toAssembly
import ru.art.platform.api.model.assembly.Assembly
import ru.art.platform.api.model.assembly.AssemblyInformation
import ru.art.platform.common.constants.ErrorCodes.ASSEMBLY_DOES_NOT_EXISTS
import ru.art.platform.common.constants.PlatformKeywords.ASSEMBLY_CAMEL_CASE
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.panel.constants.DbConstants.PROJECT_ID_INDEX_NAME
import ru.art.platform.panel.repository.ProjectRepository.getProject
import ru.art.tarantool.dao.TarantoolDao.tarantool
import java.util.*

object AssemblyRepository {
    fun putAssembly(newAssembly: Assembly): Assembly = toAssembly.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(ASSEMBLY_CAMEL_CASE, fromAssembly.map(newAssembly)))

    fun getAssembly(id: Long): Assembly = tryGetAssembly(id)
            .orElseThrow { PlatformException(ASSEMBLY_DOES_NOT_EXISTS, "Assembly with id '${id}' does not exists") }

    fun tryGetAssembly(id: Long): Optional<Assembly> = tarantool(PLATFORM_CAMEL_CASE)
            .get(ASSEMBLY_CAMEL_CASE, setOf(id))
            .map(toAssembly::map)

    fun getAssemblies() = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(ASSEMBLY_CAMEL_CASE)
            .map(toAssembly::map)

    fun getAssembliesInformation() = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(ASSEMBLY_CAMEL_CASE)
            .map(toAssemblyInformation::map)

    fun getProjectAssemblies(projectId: Long): List<Assembly> = tarantool(PLATFORM_CAMEL_CASE)
            .selectByIndex(ASSEMBLY_CAMEL_CASE, PROJECT_ID_INDEX_NAME, setOf(projectId))
            .map(toAssembly::map)

    fun getProjectAssemblies(projectIds: List<Long>): List<Assembly> = projectIds.flatMap(this::getProjectAssemblies)

    fun getProjectAssembliesInformation(projectId: Long): List<AssemblyInformation> = tarantool(PLATFORM_CAMEL_CASE)
            .selectByIndex(ASSEMBLY_CAMEL_CASE, PROJECT_ID_INDEX_NAME, setOf(projectId))
            .map(toAssemblyInformation::map)

    fun getProjectAssembliesInformation(projectIds: List<Long>): List<AssemblyInformation> = projectIds.flatMap(::getProjectAssembliesInformation)

    fun deleteAssembly(id: Long): Assembly = tarantool(PLATFORM_CAMEL_CASE)
            .delete(ASSEMBLY_CAMEL_CASE, id)
            .map(toAssembly::map)
            .orElseThrow { PlatformException(ASSEMBLY_DOES_NOT_EXISTS, "Assembly with id '${id}' does not exists") }
}
