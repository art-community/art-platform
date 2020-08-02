package ru.art.platform.specification

import ru.art.entity.CollectionMapping.collectionValueFromModel
import ru.art.entity.PrimitiveMapping.longMapper
import ru.art.platform.api.mapping.application.ApplicationIdentifierMapper.fromApplicationIdentifier
import ru.art.platform.api.mapping.filebeat.FilebeatApplicationMapper.fromFilebeatApplication
import ru.art.platform.api.mapping.filebeat.FilebeatApplicationMapper.toFilebeatApplication
import ru.art.platform.api.mapping.request.FilebeatApplicationRequestMapper.toFilebeatApplicationRequest
import ru.art.platform.api.model.filebeat.FilebeatApplication
import ru.art.platform.constants.ServiceConstants.ADD_FILEBEAT_APPLICATION
import ru.art.platform.constants.ServiceConstants.DELETE_FILEBEAT_APPLICATION
import ru.art.platform.constants.ServiceConstants.GET_APPLICATION_IDS
import ru.art.platform.constants.ServiceConstants.GET_FILEBEAT_APPLICATION
import ru.art.platform.constants.ServiceConstants.GET_FILEBEAT_APPLICATIONS
import ru.art.platform.constants.ServiceConstants.UPDATE_FILEBEAT_APPLICATION
import ru.art.platform.repository.FilebeatApplicationRepository
import ru.art.platform.service.ApplicationService
import ru.art.rsocket.function.RsocketServiceFunction.rsocket
import ru.art.service.constants.RequestValidationPolicy.NOT_NULL
import ru.art.service.constants.RequestValidationPolicy.VALIDATABLE

fun registerApplicationService() {
    rsocket(ADD_FILEBEAT_APPLICATION)
            .requestMapper(toFilebeatApplicationRequest)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromFilebeatApplication)
            .handle(ApplicationService::addFilebeatApplication)

    rsocket(UPDATE_FILEBEAT_APPLICATION)
            .requestMapper(toFilebeatApplication)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromFilebeatApplication)
            .handle(ApplicationService::updateFileBeatApplication)

    rsocket(GET_FILEBEAT_APPLICATION)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromFilebeatApplication)
            .handle<Long, FilebeatApplication>(FilebeatApplicationRepository::getFilebeatApplication)

    rsocket(GET_FILEBEAT_APPLICATIONS)
            .responseMapper(collectionValueFromModel(fromFilebeatApplication)::map)
            .produce(FilebeatApplicationRepository::getFilebeatApplications)

    rsocket(DELETE_FILEBEAT_APPLICATION)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .responseMapper(fromFilebeatApplication)
            .handle(FilebeatApplicationRepository::deleteFilebeatApplication)

    rsocket(GET_APPLICATION_IDS)
            .responseMapper(collectionValueFromModel(fromApplicationIdentifier)::map)
            .produce(ApplicationService::getApplicationIds)
}
