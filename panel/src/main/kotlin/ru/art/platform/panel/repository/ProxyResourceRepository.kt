package ru.art.platform.panel.repository

import ru.art.platform.api.mapping.resource.ProxyResourceMapper.*
import ru.art.platform.api.model.resource.*
import ru.art.platform.common.constants.*
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.common.exception.*
import ru.art.platform.panel.constants.DbConstants.PROXY_RESOURCE_SPACE
import ru.art.tarantool.dao.TarantoolDao.*
import java.util.*

object ProxyResourceRepository {
    fun putNewProxyResource(resource: ProxyResource): ProxyResource = toProxyResource.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(PROXY_RESOURCE_SPACE, fromProxyResource.map(resource.toBuilder().id(null).build())))

    fun putProxyResource(resource: ProxyResource): ProxyResource = toProxyResource.map(tarantool(PLATFORM_CAMEL_CASE)
            .put(PROXY_RESOURCE_SPACE, fromProxyResource.map(resource)))

    fun getProxyResource(id: Long): ProxyResource = tarantool(PLATFORM_CAMEL_CASE)
            .get(PROXY_RESOURCE_SPACE, setOf(id))
            .map(toProxyResource::map)
            .orElseThrow { PlatformException(ErrorCodes.RESOURCE_DOES_NOT_EXISTS, "Proxy Resource with an id '${id}' does not exist") }

    fun getProxyResource(name: String): Optional<ProxyResource> = tarantool(PLATFORM_CAMEL_CASE)
            .getByIndex(PROXY_RESOURCE_SPACE, NAME_CAMEL_CASE, setOf(name))
            .map(toProxyResource::map)

    fun deleteProxyResource(id: Long): ProxyResource = tarantool(PLATFORM_CAMEL_CASE)
            .delete(PROXY_RESOURCE_SPACE, id)
            .map(toProxyResource::map)
            .orElseThrow { PlatformException(ErrorCodes.RESOURCE_DOES_NOT_EXISTS, "Proxy Resource with an id '${id}' does not exist") }

    fun getProxyResources(): Set<ProxyResource> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(PROXY_RESOURCE_SPACE)
            .map(toProxyResource::map)
            .toSet()
}
