package ru.art.platform.panel.service

import ru.art.platform.api.model.assembly.AssemblyFilterCriteria
import ru.art.platform.api.model.assembly.AssemblyInformation
import ru.art.platform.api.model.configuration.PreparedConfiguration
import ru.art.platform.api.model.configuration.PreparedConfigurationFilterCriteria
import ru.art.platform.api.model.configuration.PreparedConfigurationIdentifier
import ru.art.platform.api.model.configuration.PreparedConfigurationRequest
import ru.art.platform.common.constants.ErrorCodes.PREPARED_CONFGURATION_ALREADY_EXISTS
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.panel.filter.AssemblyFilter
import ru.art.platform.panel.filter.PreparedConfigurationFilter
import ru.art.platform.panel.repository.PreparedConfigurationsRepository.getPreparedConfiguration
import ru.art.platform.panel.repository.PreparedConfigurationsRepository.getPreparedConfigurations
import ru.art.platform.panel.repository.PreparedConfigurationsRepository.putPreparedConfiguration

object PreparedConfigurationService {
    fun addPreparedConfiguration(request: PreparedConfigurationRequest): PreparedConfiguration {
        getPreparedConfiguration(request.projectId, request.profile, request.name).ifPresent { throw PlatformException(PREPARED_CONFGURATION_ALREADY_EXISTS) }
        val configuration = PreparedConfiguration.builder()
                .name(request.name)
                .profile(request.profile)
                .configuration(request.configuration)
                .projectId(request.projectId)
                .build()
        return putPreparedConfiguration(configuration)
    }

    fun updatePreparedConfiguration(request: PreparedConfiguration): PreparedConfiguration {
        val resource = getPreparedConfiguration(request.id)
        if (request.name != resource.name && getPreparedConfiguration(request.projectId, request.profile, request.name).isPresent) {
            throw PlatformException(PREPARED_CONFGURATION_ALREADY_EXISTS)
        }
        return putPreparedConfiguration(request)
    }

    fun getFilteredPreparedConfigurations(filterCriteria: PreparedConfigurationFilterCriteria): List<PreparedConfigurationIdentifier> =
            PreparedConfigurationFilter(filterCriteria).filter()

    fun getPreparedConfigurationIds(): List<PreparedConfigurationIdentifier> = getPreparedConfigurations().map { configuration ->
        PreparedConfigurationIdentifier.builder()
                .id(configuration.id)
                .projectId(configuration.projectId)
                .name(configuration.name)
                .profile(configuration.profile)
                .configuration(configuration.configuration)
                .build()
    }
}
