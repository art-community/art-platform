package ru.art.platform.repository

import ru.art.platform.api.mapping.resource.GitResourceMapper.*
import ru.art.platform.api.model.resource.*
import ru.art.platform.common.constants.ErrorCodes.*
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.common.exception.*
import ru.art.platform.constants.DbConstants.GIT_RESOURCE_SPACE
import ru.art.tarantool.dao.TarantoolDao.*
import java.util.*

object GitResourceRepository {
    fun putGitResource(resource: GitResource): GitResource = toGitResource.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(GIT_RESOURCE_SPACE, fromGitResource.map(resource)))

    fun getGitResource(id: Long): GitResource = tarantool(PLATFORM_CAMEL_CASE)
            .get(GIT_RESOURCE_SPACE, setOf(id))
            .map(toGitResource::map)
            .orElseThrow { PlatformException(RESOURCE_DOES_NOT_EXISTS, "Git Resource with id '${id}' does not exists") }

    fun getGitResource(name: String): Optional<GitResource> = tarantool(PLATFORM_CAMEL_CASE)
            .getByIndex(GIT_RESOURCE_SPACE, NAME_CAMEL_CASE, setOf(name))
            .map(toGitResource::map)

    fun deleteGitResource(id: Long): GitResource = tarantool(PLATFORM_CAMEL_CASE)
            .delete(GIT_RESOURCE_SPACE, id)
            .map(toGitResource::map)
            .orElseThrow { PlatformException(RESOURCE_DOES_NOT_EXISTS, "Git Resource with id '${id}' does not exists") }

    fun getGitResources(): Set<GitResource> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(GIT_RESOURCE_SPACE)
            .map(toGitResource::map)
            .toSet()
}