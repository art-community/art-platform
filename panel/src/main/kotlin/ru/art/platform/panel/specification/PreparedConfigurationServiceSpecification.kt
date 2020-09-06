package ru.art.platform.panel.specification

import ru.art.entity.CollectionMapping.collectionValueFromModel
import ru.art.entity.PrimitiveMapping.longMapper
import ru.art.platform.api.mapping.configuration.PreparedConfigurationFilterCriteriaMapper
import ru.art.platform.api.mapping.configuration.PreparedConfigurationFilterCriteriaMapper.toPreparedConfigurationFilterCriteria
import ru.art.platform.api.mapping.configuration.PreparedConfigurationIdentifierMapper.fromPreparedConfigurationIdentifier
import ru.art.platform.api.mapping.configuration.PreparedConfigurationIdentifierMapper.toPreparedConfigurationIdentifier
import ru.art.platform.api.mapping.configuration.PreparedConfigurationMapper.fromPreparedConfiguration
import ru.art.platform.api.mapping.configuration.PreparedConfigurationMapper.toPreparedConfiguration
import ru.art.platform.api.mapping.configuration.PreparedConfigurationRequestMapper.toPreparedConfigurationRequest
import ru.art.platform.api.model.configuration.PreparedConfiguration
import ru.art.platform.panel.constants.ServiceConstants.ADD_PREPARED_CONFIGURATION
import ru.art.platform.panel.constants.ServiceConstants.DELETE_PREPARED_CONFIGURATION
import ru.art.platform.panel.constants.ServiceConstants.GET_FILTERED_PREPARED_CONFIGURATIONS
import ru.art.platform.panel.constants.ServiceConstants.GET_PREPARED_CONFIGURATION
import ru.art.platform.panel.constants.ServiceConstants.GET_PREPARED_CONFIGURATIONS
import ru.art.platform.panel.constants.ServiceConstants.GET_PREPARED_CONFIGURATION_IDS
import ru.art.platform.panel.constants.ServiceConstants.UPDATE_PREPARED_CONFIGURATION
import ru.art.platform.panel.repository.PreparedConfigurationsRepository
import ru.art.platform.panel.service.PreparedConfigurationService
import ru.art.rsocket.function.RsocketServiceFunction.rsocket
import ru.art.service.constants.RequestValidationPolicy.NOT_NULL
import ru.art.service.constants.RequestValidationPolicy.VALIDATABLE

fun registerPreparedConfigurationService() {
    rsocket(ADD_PREPARED_CONFIGURATION)
            .requestMapper(toPreparedConfigurationRequest)
            .validationPolicy(VALIDATABLE)
            .responseMapper(fromPreparedConfiguration)
            .handle(PreparedConfigurationService::addPreparedConfiguration)

    rsocket(UPDATE_PREPARED_CONFIGURATION)
            .requestMapper(toPreparedConfiguration)
            .validationPolicy(NOT_NULL)
            .responseMapper(fromPreparedConfiguration)
            .handle(PreparedConfigurationService::updatePreparedConfiguration)

    rsocket(GET_PREPARED_CONFIGURATION)
            .requestMapper(longMapper.toModel)
            .responseMapper(fromPreparedConfiguration)
            .handle<Long, PreparedConfiguration>(PreparedConfigurationsRepository::getPreparedConfiguration)

    rsocket(GET_PREPARED_CONFIGURATIONS)
            .responseMapper(collectionValueFromModel(fromPreparedConfiguration)::map)
            .produce(PreparedConfigurationsRepository::getPreparedConfigurations)

    rsocket(GET_FILTERED_PREPARED_CONFIGURATIONS)
            .requestMapper(toPreparedConfigurationFilterCriteria)
            .responseMapper(collectionValueFromModel(fromPreparedConfigurationIdentifier)::map)
            .handle(PreparedConfigurationService::getFilteredPreparedConfigurations)

    rsocket(GET_PREPARED_CONFIGURATION_IDS)
            .responseMapper(collectionValueFromModel(fromPreparedConfigurationIdentifier)::map)
            .produce(PreparedConfigurationService::getPreparedConfigurationIds)

    rsocket(DELETE_PREPARED_CONFIGURATION)
            .requestMapper(longMapper.toModel)
            .validationPolicy(NOT_NULL)
            .responseMapper(fromPreparedConfiguration)
            .handle(PreparedConfigurationsRepository::deletePreparedConfiguration)
}
