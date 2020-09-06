package ru.art.platform.factory

import ru.art.platform.api.model.application.ApplicationIdentifier
import ru.art.platform.common.constants.Applications.FILEBEAT_APPLICATION

object ApplicationIdFactory {
    fun filebeatApplicationId(id: Long, name: String): ApplicationIdentifier = ApplicationIdentifier.builder()
            .id(id)
            .name(name)
            .type(FILEBEAT_APPLICATION)
            .build()
}
