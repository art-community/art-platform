package ru.art.platform.service

import reactor.core.Disposable
import ru.art.platform.api.mapping.project.ProjectMapper.toProject
import ru.art.platform.api.model.project.Project
import ru.art.platform.api.model.request.BuildRequest
import ru.art.platform.api.model.request.ProjectChangesRequest
import ru.art.platform.common.constants.EventTypes.UPDATE_EVENT
import ru.art.platform.common.constants.States.PROJECT_INITIALIZED_STATE
import ru.art.platform.extensions.ifNotEmpty
import ru.art.platform.repository.AssemblyConfigurationRepository.getAssemblyConfiguration
import ru.art.platform.service.AssemblyService.buildProject
import ru.art.platform.service.ProjectService.computeProjectChanges
import ru.art.platform.service.ProjectService.reloadProject
import ru.art.platform.service.ProjectService.subscribeOnProject
import java.util.concurrent.atomic.AtomicReference

object AutomationService {
    fun handleProjectsChanges(projects: List<Project>, reference: String) = projects.forEach { project ->
        val streamReference: AtomicReference<Disposable> = AtomicReference()
        val stream = subscribeOnProject()
                .filter { event -> event.type == UPDATE_EVENT }
                .map { event -> toProject.map(event.data) }
                .filter { reloadedProject -> reloadedProject.state == PROJECT_INITIALIZED_STATE && reloadedProject.id == project.id }
                .subscribe { reloadedProject ->
                    streamReference.get()?.dispose()
                    rebuildChangedArtifacts(reloadedProject, reference)
                }
        streamReference.set(stream)
        reloadProject(project.id)
    }

    private fun rebuildChangedArtifacts(reloadedProject: Project, reference: String) {
        computeProjectChanges(ProjectChangesRequest.builder().projectId(reloadedProject.id).reference(reference).build())
                .subscribe { changes ->
                    getAssemblyConfiguration(reloadedProject.id).let { configuration ->
                        configuration.artifactConfigurations
                                .filter { artifactConfiguration -> changes.changedArtifacts.contains(artifactConfiguration.artifact) }
                                .ifNotEmpty { configurations ->
                                    buildProject(BuildRequest.builder()
                                            .projectId(reloadedProject.id)
                                            .resourceId(configuration.defaultResourceId)
                                            .artifactConfigurations(configurations)
                                            .version(reloadedProject.versions.find { version -> version.reference.equals(reference, true) })
                                            .build())
                                }
                    }
                }
    }

    private fun rebuildAllArtifacts(reloadedProject: Project, reference: String) {
        getAssemblyConfiguration(reloadedProject.id).let { configuration ->
            configuration.artifactConfigurations
                    .ifNotEmpty { configurations ->
                        buildProject(BuildRequest.builder()
                                .projectId(reloadedProject.id)
                                .resourceId(configuration.defaultResourceId)
                                .artifactConfigurations(configurations)
                                .version(reloadedProject.versions.find { version -> version.reference.equals(reference, true) })
                                .build())
                    }
        }
    }
}
