package ru.art.platform.repository

import ru.art.platform.api.mapping.project.ProjectMapper.fromProject
import ru.art.platform.api.mapping.project.ProjectMapper.toProject
import ru.art.platform.api.model.project.Project
import ru.art.platform.common.constants.ErrorCodes.PROJECT_DOES_NOT_EXISTS
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.common.constants.States
import ru.art.platform.common.constants.States.PROJECT_INITIALIZED_STATE
import ru.art.platform.common.exception.PlatformException
import ru.art.tarantool.dao.TarantoolDao.tarantool
import java.util.*

object ProjectRepository {
    fun getProject(id: Long): Project = tryGetProject(id)
            .orElseThrow { PlatformException(PROJECT_DOES_NOT_EXISTS, "Project with id '${id}' does not exists") }

    fun putProject(newProject: Project): Project = toProject.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(PROJECT_CAMEL_CASE, fromProject.map(newProject)))

    fun tryGetProject(id: Long): Optional<Project> = tarantool(PLATFORM_CAMEL_CASE)
            .get(PROJECT_CAMEL_CASE, setOf(id))
            .map(toProject::map)

    fun getProject(name: String): Optional<Project> = tarantool(PLATFORM_CAMEL_CASE)
            .getByIndex(PROJECT_CAMEL_CASE, NAME_CAMEL_CASE, setOf(name))
            .map(toProject::map)

    fun getProjects(): Set<Project> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(PROJECT_CAMEL_CASE)
            .map(toProject::map)
            .toSet()

    fun getInitializedProjects(): Set<Project> = getProjects()
            .filter { project -> project.state == PROJECT_INITIALIZED_STATE }
            .toSet()

    fun deleteProject(id: Long): Project = tarantool(PLATFORM_CAMEL_CASE)
            .delete(PROJECT_CAMEL_CASE, id)
            .map(toProject::map)
            .orElseThrow { PlatformException(PROJECT_DOES_NOT_EXISTS, "Project with id '${id}' does not exists") }
}
