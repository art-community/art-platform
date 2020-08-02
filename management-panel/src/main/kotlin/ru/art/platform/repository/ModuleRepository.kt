package ru.art.platform.repository

import ru.art.platform.api.mapping.module.ModuleInformationMapper.toModuleInformation
import ru.art.platform.api.mapping.module.ModuleMapper
import ru.art.platform.api.mapping.module.ModuleMapper.fromModule
import ru.art.platform.api.mapping.module.ModuleMapper.toModule
import ru.art.platform.api.model.module.Module
import ru.art.platform.api.model.module.ModuleInformation
import ru.art.platform.common.constants.ErrorCodes.MODULE_DOES_NOT_EXISTS
import ru.art.platform.common.constants.PlatformKeywords.MODULE_CAMEL_CASE
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.constants.DbConstants.PROJECT_ID_INDEX_NAME
import ru.art.platform.repository.ProjectRepository.getProject
import ru.art.tarantool.dao.TarantoolDao.tarantool
import java.util.*

object ModuleRepository {
    fun putModule(module: Module): Module = toModule.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(MODULE_CAMEL_CASE, fromModule.map(module)))


    fun getModule(id: Long): Module = tryGetModule(id)
            .orElseThrow { PlatformException(MODULE_DOES_NOT_EXISTS, "Module with id '${id} does not exists") }

    fun tryGetModule(id: Long): Optional<Module> = tarantool(PLATFORM_CAMEL_CASE).get(MODULE_CAMEL_CASE, setOf(id)).map(toModule::map)

    fun getProjectModules(projectId: Long): List<Module> = tarantool(PLATFORM_CAMEL_CASE)
            .selectByIndex(MODULE_CAMEL_CASE, PROJECT_ID_INDEX_NAME, setOf(projectId))
            .map(toModule::map)

    fun getProjectModules(projectIds: List<Long>): List<Module> = projectIds.flatMap(::getProjectModules)

    fun getProjectModulesInformation(projectId: Long): List<ModuleInformation> = tarantool(PLATFORM_CAMEL_CASE)
            .selectByIndex(MODULE_CAMEL_CASE, PROJECT_ID_INDEX_NAME, setOf(projectId))
            .map(toModuleInformation::map)

    fun getProjectModulesInformation(projectIds: List<Long>): List<ModuleInformation> = projectIds.flatMap(::getProjectModulesInformation)

    fun getModules(): List<Module> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(MODULE_CAMEL_CASE)
            .map(toModule::map)

    fun getModules(ids: List<Long>): List<Module> = ids.map(this::getModule)

    fun getModuleInformation(id: Long): ModuleInformation = tarantool(PLATFORM_CAMEL_CASE)
            .get(MODULE_CAMEL_CASE, setOf(id))
            .map(toModuleInformation::map)
            .orElseThrow { PlatformException(MODULE_DOES_NOT_EXISTS, "Module with id '${id} does not exists") }

    fun getModulesInformation(ids: List<Long>): List<ModuleInformation> = ids.map(::getModuleInformation)

    fun getModulesInformation(): List<ModuleInformation> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(MODULE_CAMEL_CASE)
            .map(toModuleInformation::map)

    fun deleteModule(id: Long): Module = tarantool(PLATFORM_CAMEL_CASE)
            .delete(MODULE_CAMEL_CASE, setOf(id))
            .map(toModule::map)
            .orElseThrow { PlatformException(MODULE_DOES_NOT_EXISTS, "Module with id '${id} does not exists") }
}
