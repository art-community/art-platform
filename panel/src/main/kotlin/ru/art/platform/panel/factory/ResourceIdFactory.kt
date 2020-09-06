package ru.art.platform.panel.factory

import ru.art.platform.api.model.resource.*
import ru.art.platform.common.constants.Resources.*

object ResourceIdFactory {
    fun gitResourceId(id: Long, name: String): ResourceIdentifier = ResourceIdentifier.builder().id(id).name(name).type(GIT_RESOURCE).build()

    fun artifactsResourceId(id: Long, name: String): ResourceIdentifier = ResourceIdentifier.builder().id(id).name(name).type(ARTIFACTS_RESOURCE).build()

    fun platformResourceId(id: Long, name: String): ResourceIdentifier = ResourceIdentifier.builder().id(id).name(name).type(PLATFORM_RESOURCE).build()

    fun openShiftResourceId(id: Long, name: String): ResourceIdentifier = ResourceIdentifier.builder().id(id).name(name).type(OPEN_SHIFT_RESOURCE).build()

    fun proxyResourceId(id: Long, name: String): ResourceIdentifier = ResourceIdentifier.builder().id(id).name(name).type(PROXY_RESOURCE).build()
}
