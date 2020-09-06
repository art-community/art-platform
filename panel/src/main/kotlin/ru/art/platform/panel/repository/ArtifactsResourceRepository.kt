package ru.art.platform.panel.repository

import ru.art.platform.api.mapping.resource.ArtifactsResourceMapper.*
import ru.art.platform.api.model.resource.*
import ru.art.platform.common.constants.*
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.common.exception.*
import ru.art.platform.panel.constants.DbConstants.ARTIFACTS_RESOURCE_SPACE
import ru.art.tarantool.dao.TarantoolDao.*
import java.util.*

object ArtifactsResourceRepository {
    fun putNewArtifactsResource(resource: ArtifactsResource): ArtifactsResource = toArtifactsResource.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(ARTIFACTS_RESOURCE_SPACE, fromArtifactsResource.map(resource.toBuilder().id(null).build())))

    fun putArtifactsResource(resource: ArtifactsResource): ArtifactsResource = toArtifactsResource.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(ARTIFACTS_RESOURCE_SPACE, fromArtifactsResource.map(resource)))

    fun getArtifactsResource(id: Long): ArtifactsResource = tarantool(PLATFORM_CAMEL_CASE)
            .get(ARTIFACTS_RESOURCE_SPACE, setOf(id))
            .map(toArtifactsResource::map)
            .orElseThrow { PlatformException(ErrorCodes.RESOURCE_DOES_NOT_EXISTS, "Artifacts Resource with an id '${id}' does not exist") }

    fun getArtifactsResource(name: String): Optional<ArtifactsResource> = tarantool(PLATFORM_CAMEL_CASE)
            .getByIndex(ARTIFACTS_RESOURCE_SPACE, NAME_CAMEL_CASE, setOf(name))
            .map(toArtifactsResource::map)

    fun deleteArtifactsResource(id: Long): ArtifactsResource = tarantool(PLATFORM_CAMEL_CASE)
            .delete(ARTIFACTS_RESOURCE_SPACE, id)
            .map(toArtifactsResource::map)
            .orElseThrow { PlatformException(ErrorCodes.RESOURCE_DOES_NOT_EXISTS, "Artifacts Resource with an id '${id}' does not exist") }

    fun getArtifactsResources(): Set<ArtifactsResource> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(ARTIFACTS_RESOURCE_SPACE)
            .map(toArtifactsResource::map)
            .toSet()
}
