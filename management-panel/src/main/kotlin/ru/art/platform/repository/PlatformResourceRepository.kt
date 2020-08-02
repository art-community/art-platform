package ru.art.platform.repository

import ru.art.platform.api.mapping.resource.PlatformResourceMapper.fromPlatformResource
import ru.art.platform.api.mapping.resource.PlatformResourceMapper.toPlatformResource
import ru.art.platform.api.model.resource.PlatformResource
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.constants.DbConstants.PLATFORM_RESOURCE_SPACE
import ru.art.tarantool.dao.TarantoolDao.tarantool

object PlatformResourceRepository {
    fun getPlatformResources(): Set<PlatformResource> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(PLATFORM_RESOURCE_SPACE)
            .map(toPlatformResource::map)
            .toSet()

    fun putPlatformResource(resource: PlatformResource): PlatformResource =
            toPlatformResource.map(tarantool(PLATFORM_CAMEL_CASE).put(PLATFORM_RESOURCE_SPACE, fromPlatformResource.map(resource)))
}