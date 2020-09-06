package ru.art.platform.filter

import ru.art.platform.api.model.configuration.PreparedConfigurationFilterCriteria
import ru.art.platform.api.model.configuration.PreparedConfigurationIdentifier
import ru.art.platform.service.PreparedConfigurationService.getPreparedConfigurationIds
import java.util.stream.Collectors.toList
import java.util.stream.Stream

class PreparedConfigurationFilter(private val criteria: PreparedConfigurationFilterCriteria) {
    fun filter(): List<PreparedConfigurationIdentifier> {
        val ids = getPreparedConfigurationIds()
        return when {
            !criteria.projectIds.isNullOrEmpty() ->
                filterByNames(filterByProfiles(filterByProjects(ids.stream()))).collect(toList())
            !criteria.profiles.isNullOrEmpty() ->
                filterByProfiles(filterByProjects(ids.stream())).collect(toList())
            !criteria.names.isNullOrEmpty() ->
                filterByProjects(ids.stream()).collect(toList())
            else ->
                ids
        }
    }

    private fun filterByProjects(stream: Stream<PreparedConfigurationIdentifier>): Stream<PreparedConfigurationIdentifier> {
        if (!criteria.projectIds.isNullOrEmpty()) {
            return stream.filter { id -> id.projectId in criteria.projectIds }
        }
        return stream
    }

    private fun filterByNames(stream: Stream<PreparedConfigurationIdentifier>): Stream<PreparedConfigurationIdentifier> {
        if (!criteria.names.isNullOrEmpty()) {
            return stream.filter { id -> id.name in criteria.names }
        }
        return stream
    }

    private fun filterByProfiles(stream: Stream<PreparedConfigurationIdentifier>): Stream<PreparedConfigurationIdentifier> {
        if (!criteria.profiles.isNullOrEmpty()) {
            return stream.filter { id -> id.profile in criteria.profiles }
        }
        return stream
    }
}
