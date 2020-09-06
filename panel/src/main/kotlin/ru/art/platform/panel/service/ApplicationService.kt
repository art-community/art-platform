package ru.art.platform.panel.service

import ru.art.platform.api.model.application.ApplicationIdentifier
import ru.art.platform.api.model.filebeat.FilebeatApplication
import ru.art.platform.api.model.request.FilebeatApplicationRequest
import ru.art.platform.common.constants.ErrorCodes.APPLICATION_ALREADY_EXISTS
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.panel.factory.ApplicationIdFactory.filebeatApplicationId
import ru.art.platform.panel.repository.FilebeatApplicationRepository.getFilebeatApplication
import ru.art.platform.panel.repository.FilebeatApplicationRepository.getFilebeatApplications
import ru.art.platform.panel.repository.FilebeatApplicationRepository.putFilebeatApplication


object ApplicationService {
    fun addFilebeatApplication(request: FilebeatApplicationRequest): FilebeatApplication {
        getFilebeatApplication(request.name).ifPresent { throw PlatformException(APPLICATION_ALREADY_EXISTS) }
        val resource = FilebeatApplication.builder()
                .name(request.name)
                .resourceId(request.resourceId)
                .url(request.url)
                .build()
        return putFilebeatApplication(resource)
    }

    fun updateFileBeatApplication(request: FilebeatApplication): FilebeatApplication {
        val application = getFilebeatApplication(request.id)
        if (request.name != application.name && getFilebeatApplication(request.name).isPresent) {
            throw PlatformException(APPLICATION_ALREADY_EXISTS)
        }
        return putFilebeatApplication(request)
    }

    fun getApplicationIds(): Set<ApplicationIdentifier> = getFilebeatApplications()
            .map { resource -> filebeatApplicationId(resource.id, resource.name) }
            .toSet()
}
