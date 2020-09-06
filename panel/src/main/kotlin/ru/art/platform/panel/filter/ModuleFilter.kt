package ru.art.platform.panel.filter

import ru.art.platform.api.model.module.ModuleFilterCriteria
import ru.art.platform.api.model.module.ModuleInformation
import ru.art.platform.common.constants.States.*
import ru.art.platform.panel.repository.ModuleRepository.getModulesInformation
import ru.art.platform.panel.repository.ModuleRepository.getProjectModulesInformation
import java.util.stream.Collectors.toList
import java.util.stream.Stream


class ModuleFilter(private val criteria: ModuleFilterCriteria) {
    fun filter(): List<ModuleInformation> {
        val modulesInformation = getModulesInformation()
        return when {
            !criteria.ids.isNullOrEmpty() ->
                sort(filterByVersion(filterByStates(filterByProjects(getModulesInformation(criteria.ids).stream())))).collect(toList())
            !criteria.projectIds.isNullOrEmpty() ->
                sort(filterByVersion(filterByStates(getProjectModulesInformation(criteria.projectIds).stream()))).collect(toList())
            !criteria.states.isNullOrEmpty() ->
                sort(filterByVersion(filterByStates(modulesInformation.stream()))).collect(toList())
            !criteria.versions.isNullOrEmpty() ->
                sort(filterByVersion(modulesInformation.stream())).collect(toList())
            else ->
                sort(modulesInformation.stream()).collect(toList())
        }
    }

    private fun filterByProjects(stream: Stream<ModuleInformation>): Stream<ModuleInformation> {
        if (!criteria.projectIds.isNullOrEmpty()) {
            return stream.filter { module -> module.projectId in criteria.projectIds }
        }
        return stream
    }

    private fun filterByStates(stream: Stream<ModuleInformation>): Stream<ModuleInformation> {
        if (!criteria.states.isNullOrEmpty()) {
            return stream.filter { module ->
                when (module.state) {
                    MODULE_INSTALLATION_STARTED_STATE -> criteria.states.contains(MODULE_INSTALLING_STATE)
                    MODULE_UPDATE_STARTED_STATE -> criteria.states.contains(MODULE_UPDATING_STATE)
                    MODULE_STOP_STARTED_STATE -> criteria.states.contains(MODULE_STOPPING_STATE)
                    MODULE_RESTART_STARTED_STATE -> criteria.states.contains(MODULE_RESTARTING_STATE)
                    MODULE_UNINSTALL_STARTED_STATE -> criteria.states.contains(MODULE_UNINSTALLING_STATE)

                    MODULE_NOT_INSTALLED_STATE -> module.state in criteria.states
                    MODULE_INVALID_STATE -> module.state in criteria.states
                    MODULE_STOPPED_STATE -> module.state in criteria.states
                    MODULE_RUN_STATE -> module.state in criteria.states
                    MODULE_INSTALLING_STATE -> module.state in criteria.states
                    MODULE_UPDATING_STATE -> module.state in criteria.states
                    MODULE_STOPPING_STATE -> module.state in criteria.states
                    MODULE_RESTARTING_STATE -> module.state in criteria.states
                    MODULE_UNINSTALLING_STATE -> module.state in criteria.states
                    else -> false
                }
            }
        }
        return stream
    }

    private fun filterByVersion(stream: Stream<ModuleInformation>): Stream<ModuleInformation> {
        if (!criteria.versions.isNullOrEmpty()) {
            return stream.filter { module -> module.artifact.version in criteria.versions }
        }
        return stream
    }

    private fun sort(stream: Stream<ModuleInformation>): Stream<ModuleInformation> =
            if (criteria.sorted == true) {
                stream.sorted { current, next ->
                    if (current.state == next.state) {
                        return@sorted if (next.updateTimeStamp > current.updateTimeStamp) 1 else -1
                    }
                    when (next.state) {
                        MODULE_INVALID_STATE -> return@sorted -1
                        MODULE_NOT_INSTALLED_STATE -> return@sorted when (current.state) {
                            MODULE_INVALID_STATE -> 1
                            else -> -1
                        }
                        MODULE_STOPPED_STATE -> return@sorted when (current.state) {
                            MODULE_INVALID_STATE,
                            MODULE_NOT_INSTALLED_STATE -> 1
                            else -> -1
                        }
                        MODULE_RUN_STATE -> return@sorted when (current.state) {
                            MODULE_INVALID_STATE,
                            MODULE_NOT_INSTALLED_STATE,
                            MODULE_STOPPED_STATE -> 1
                            else -> -1
                        }
                        MODULE_UPDATE_STARTED_STATE,
                        MODULE_UPDATING_STATE,
                        MODULE_STOP_STARTED_STATE,
                        MODULE_STOPPING_STATE,
                        MODULE_RESTART_STARTED_STATE,
                        MODULE_RESTARTING_STATE,
                        MODULE_UNINSTALL_STARTED_STATE,
                        MODULE_UNINSTALLING_STATE -> return@sorted when (current.state) {
                            MODULE_INVALID_STATE,
                            MODULE_NOT_INSTALLED_STATE,
                            MODULE_RUN_STATE,
                            MODULE_STOPPED_STATE -> 1
                            else -> -1
                        }
                        MODULE_INSTALLATION_STARTED_STATE,
                        MODULE_INSTALLING_STATE -> return@sorted when (current.state) {
                            MODULE_INVALID_STATE,
                            MODULE_NOT_INSTALLED_STATE,
                            MODULE_STOPPED_STATE,
                            MODULE_UPDATE_STARTED_STATE,
                            MODULE_UPDATING_STATE,
                            MODULE_STOP_STARTED_STATE,
                            MODULE_STOPPING_STATE,
                            MODULE_RESTART_STARTED_STATE,
                            MODULE_RESTARTING_STATE,
                            MODULE_UNINSTALL_STARTED_STATE,
                            MODULE_UNINSTALLING_STATE,
                            MODULE_RUN_STATE -> 1
                            else -> -1
                        }
                    }
                    return@sorted 0
                }
            } else stream
}
