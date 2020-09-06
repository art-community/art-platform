package ru.art.platform.panel.filter

import ru.art.platform.api.model.assembly.AssemblyFilterCriteria
import ru.art.platform.api.model.assembly.AssemblyInformation
import ru.art.platform.panel.repository.AssemblyRepository.getAssembliesInformation
import ru.art.platform.panel.repository.AssemblyRepository.getProjectAssembliesInformation
import java.util.stream.Collectors.toList
import java.util.stream.Stream

class AssemblyFilter(private val criteria: AssemblyFilterCriteria) {
    fun filter(): List<AssemblyInformation> {
        val assemblies = getAssembliesInformation()
        return when {
            !criteria.projectIds.isNullOrEmpty() ->
                limit(sort(filterByVersions(filterByStates(filterByProjects(getProjectAssembliesInformation(criteria.projectIds).stream()))))).collect(toList())
            !criteria.states.isNullOrEmpty() ->
                limit(sort(filterByVersions(filterByStates(filterByProjects(assemblies.stream()))))).collect(toList())
            !criteria.versions.isNullOrEmpty() ->
                limit(sort(filterByVersions(filterByStates(filterByProjects(assemblies.stream()))))).collect(toList())
            else ->
                limit(sort(assemblies.stream())).collect(toList())
        }
    }

    private fun filterByProjects(stream: Stream<AssemblyInformation>): Stream<AssemblyInformation> {
        if (!criteria.projectIds.isNullOrEmpty()) {
            return stream.filter { assembly -> assembly.projectId in criteria.projectIds }
        }
        return stream
    }

    private fun filterByStates(stream: Stream<AssemblyInformation>): Stream<AssemblyInformation> {
        if (!criteria.states.isNullOrEmpty()) {
            return stream.filter { assembly -> assembly.state in criteria.states }
        }
        return stream
    }

    private fun filterByVersions(stream: Stream<AssemblyInformation>): Stream<AssemblyInformation> {
        if (!criteria.versions.isNullOrEmpty()) {
            return stream.filter { assembly -> assembly.version.version in criteria.versions }
        }
        return stream
    }

    private fun sort(stream: Stream<AssemblyInformation>): Stream<AssemblyInformation> =
            if (criteria.sorted == true) {
                stream.sorted(compareByDescending { assembly -> assembly.startTimeStamp })
            } else stream

    private fun limit(stream: Stream<AssemblyInformation>): Stream<AssemblyInformation> = criteria.count
            ?.let { count -> stream.limit(count.toLong()) }
            ?: stream
}
