package ru.art.platform.panel.repository

import ru.art.platform.api.mapping.resource.OpenShiftResourceMapper.*
import ru.art.platform.api.model.resource.*
import ru.art.platform.common.constants.*
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.common.exception.*
import ru.art.platform.panel.constants.DbConstants.OPEN_SHIFT_RESOURCE_SPACE
import ru.art.tarantool.dao.TarantoolDao.*
import java.util.*

object OpenShiftResourceRepository {
    fun getOpenShiftResources(): Set<OpenShiftResource> = tarantool(PLATFORM_CAMEL_CASE)
            .selectAll(OPEN_SHIFT_RESOURCE_SPACE)
            .map(toOpenShiftResource::map)
            .toSet()

    fun putNewOpenShiftResource(resource: OpenShiftResource): OpenShiftResource =
            toOpenShiftResource.map(tarantool(PLATFORM_CAMEL_CASE).put(OPEN_SHIFT_RESOURCE_SPACE, fromOpenShiftResource.map(resource.toBuilder().id(null).build())))

    fun putOpenShiftResource(resource: OpenShiftResource): OpenShiftResource =
            toOpenShiftResource.map(tarantool(PLATFORM_CAMEL_CASE).put(OPEN_SHIFT_RESOURCE_SPACE, fromOpenShiftResource.map(resource)))

    fun getOpenShiftResource(id: Long): OpenShiftResource = tarantool(PLATFORM_CAMEL_CASE)
            .get(OPEN_SHIFT_RESOURCE_SPACE, setOf(id))
            .map(toOpenShiftResource::map)
            .orElseThrow { PlatformException(ErrorCodes.RESOURCE_DOES_NOT_EXISTS, "OpenShift Resource with id '${id}' does not exists") }

    fun getOpenShiftResource(name: String): Optional<OpenShiftResource> = tarantool(PLATFORM_CAMEL_CASE)
            .getByIndex(OPEN_SHIFT_RESOURCE_SPACE, NAME_CAMEL_CASE, setOf(name))
            .map(toOpenShiftResource::map)

    fun deleteOpenShiftResource(id: Long): OpenShiftResource = tarantool(PLATFORM_CAMEL_CASE)
            .delete(OPEN_SHIFT_RESOURCE_SPACE, id)
            .map(toOpenShiftResource::map)
            .orElseThrow { PlatformException(ErrorCodes.RESOURCE_DOES_NOT_EXISTS, "OpenShift Resource with id '${id}' does not exists") }
}
