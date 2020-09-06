package ru.art.platform.panel.service

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.art.platform.panel.agent.initialzier.createProjectInitializer
import ru.art.platform.api.model.assembly.AssembledArtifact
import ru.art.platform.api.model.project.Project
import ru.art.platform.api.model.project.ProjectChanges
import ru.art.platform.api.model.request.*
import ru.art.platform.panel.broker.deleted
import ru.art.platform.panel.broker.projectConsumer
import ru.art.platform.panel.broker.updated
import ru.art.platform.panel.client.connectToAgent
import ru.art.platform.common.broker.PlatformEvent
import ru.art.platform.common.constants.ErrorCodes.PROJECT_ALREADY_EXISTS
import ru.art.platform.common.constants.Resources.ARTIFACTS_RESOURCE
import ru.art.platform.common.constants.Resources.OPEN_SHIFT_RESOURCE
import ru.art.platform.common.constants.States.*
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.panel.factory.ProjectFactory.createProject
import ru.art.platform.panel.factory.ResourceIdFactory.artifactsResourceId
import ru.art.platform.panel.factory.ResourceIdFactory.openShiftResourceId
import ru.art.platform.panel.repository.ArtifactsResourceRepository.getArtifactsResources
import ru.art.platform.panel.repository.ArtifactsResourceRepository.putNewArtifactsResource
import ru.art.platform.panel.repository.AssemblyRepository.getProjectAssemblies
import ru.art.platform.panel.repository.ExternalArtifactRepository
import ru.art.platform.panel.repository.ExternalArtifactRepository.getExternalArtifacts
import ru.art.platform.panel.repository.GitResourceRepository.getGitResource
import ru.art.platform.panel.repository.OpenShiftResourceRepository.getOpenShiftResources
import ru.art.platform.panel.repository.OpenShiftResourceRepository.putNewOpenShiftResource
import ru.art.platform.panel.repository.ProjectRepository
import ru.art.platform.panel.repository.ProjectRepository.getProject
import ru.art.platform.panel.repository.ProjectRepository.getProjects
import ru.art.platform.panel.repository.ProjectRepository.putProject
import ru.art.platform.panel.repository.UserRepository.getUser
import ru.art.platform.panel.repository.UserRepository.putUser
import ru.art.platform.panel.service.AssemblyService.deleteProjectAssemblies
import ru.art.platform.panel.service.ModuleService.deleteProjectModules
import ru.art.platform.panel.service.ProjectInitializationService.initializeProject
import ru.art.platform.panel.service.ProjectInitializationService.reloadProject
import ru.art.platform.panel.state.SessionState.localUser
import java.lang.Integer.MAX_VALUE
import kotlin.random.Random.Default.nextInt

object ProjectService {
    fun addProject(request: ProjectRequest): Project {
        getProject(request.name).ifPresent { throw PlatformException(PROJECT_ALREADY_EXISTS) }
        val project = putProject(createProject(request))
        putUser(getUser(localUser.get().id).toBuilder().availableProject(project.id).build()).updated()
        initializeProject(project, createProjectInitializer(request.initializationResourceId, project))
        return project
    }

    fun computeProjectChanges(request: ProjectChangesRequest): Mono<ProjectChanges> {
        val project = getProject(request.projectId)
        val changesRequest = AgentProjectChangesRequest.builder()
                .project(project)
                .gitResource(getGitResource(project.gitResourceId.id))
                .reference(request.reference)
                .build()
        val agent = createProjectInitializer(project.initializationResourceId, project).startAgent()
        return connectToAgent(agent).computeProjectChanges(changesRequest)
    }

    fun reloadProject(id: Long): Project {
        var project = getProject(id).toBuilder()
                .clearArtifacts()
                .clearVersions()
                .clearTechnologies()
                .state(PROJECT_RELOAD_STARTED_STATE)
                .build()
        project = putProject(project).updated()
        reloadProject(project, createProjectInitializer(project.initializationResourceId, project))
        return project
    }

    fun updateProject(request: ProjectUpdateRequest): Project {
        val project = getProject(request.id)
        if (request.name != project.name && getProject(request.name).isPresent) {
            throw PlatformException(PROJECT_ALREADY_EXISTS)
        }
        return putProject(project.toBuilder()
                .name(request.name)
                .openShiftConfiguration(request.openShiftConfiguration)
                .notificationsConfiguration(request.notificationsConfiguration)
                .build())
                .updated()
    }

    fun subscribeOnProject(): Flux<PlatformEvent> = projectConsumer()

    fun failStuckProjects() = getProjects()
            .filter { project ->
                project.state == PROJECT_CREATED_STATE
                        || project.state == PROJECT_RELOAD_STARTED_STATE
                        || project.state == PROJECT_RELOADING_STATE
            }
            .forEach { project -> putProject(project.toBuilder().state(PROJECT_INITIALIZATION_FAILED_STATE).build()) }

    fun getAssembledProjectArtifacts(request: AssembledProjectArtifactsRequest): Set<AssembledArtifact> {
        val external = getExternalArtifacts(request.projectId).map { artifact ->
            AssembledArtifact.builder()
                    .name(artifact.name)
                    .version(artifact.version)
                    .externalId(artifact.externalId)
                    .build()
        }
        val internal = getProjectAssemblies(request.projectId).flatMap { assembly -> assembly.artifacts }
        val artifacts = internal + external
        if (!request.version.isNullOrBlank()) {
            return artifacts.filter { artifact -> artifact.version == request.version }.toSet()
        }
        return artifacts.toSet()
    }

    fun addExternalArtifacts(request: ExternalArtifactsRequest) {
        val currentArtifactsResources = getArtifactsResources()
        val currentOpenShiftResources = getOpenShiftResources()
        val artifactsResourceIds = request.artifactsResources.map { resource ->
            val current = currentArtifactsResources.find { current ->
                current.userName == resource.userName &&
                        current.url == resource.url &&
                        current.password == resource.password
            }
            current?.apply {
                if (current.id == resource.id) {
                    return@map resource.id to artifactsResourceId(resource.id, resource.name)
                }
                if (current.name == resource.name) {
                    val newResource = putNewArtifactsResource(resource.toBuilder().name("${current.name}-${nextInt(MAX_VALUE)}").build())
                    return@map resource.id to artifactsResourceId(newResource.id, newResource.name)
                }
                val newResource = putNewArtifactsResource(resource)
                return@map resource.id to artifactsResourceId(newResource.id, newResource.name)
            }

            if (currentArtifactsResources.any { currentArtifactsResource -> currentArtifactsResource.name == resource.name }) {
                val newResource = putNewArtifactsResource(resource.toBuilder().name("${resource.name}-${nextInt(MAX_VALUE)}").build())
                return@map resource.id to artifactsResourceId(newResource.id, newResource.name)
            }

            val newResource = putNewArtifactsResource(resource)
            return@map resource.id to artifactsResourceId(newResource.id, newResource.name)
        }.toMap()

        val openShiftResourceIds = request.openShiftResources.map { resource ->
            val current = currentOpenShiftResources.find { current ->
                current.userName == resource.userName &&
                        current.apiUrl == resource.apiUrl &&
                        current.applicationsDomain == resource.applicationsDomain &&
                        current.privateRegistryUrl == resource.privateRegistryUrl &&
                        current.password == resource.password
            }
            current?.apply {
                if (current.id == resource.id) {
                    return@map resource.id to openShiftResourceId(resource.id, resource.name)
                }
                if (current.name == resource.name) {
                    val newResource = putNewOpenShiftResource(resource.toBuilder().name("${current.name}-${nextInt(MAX_VALUE)}").build())
                    return@map resource.id to openShiftResourceId(newResource.id, newResource.name)
                }
                val newResource = putNewOpenShiftResource(resource)
                return@map resource.id to openShiftResourceId(newResource.id, newResource.name)
            }
            if (currentOpenShiftResources.any { currentOpenShiftResource -> currentOpenShiftResource.name == resource.name }) {
                val newResource = putNewOpenShiftResource(resource.toBuilder().name("${resource.name}-${nextInt(MAX_VALUE)}").build())
                return@map resource.id to openShiftResourceId(newResource.id, newResource.name)
            }
            val newResource = putNewOpenShiftResource(resource)
            return@map resource.id to openShiftResourceId(newResource.id, newResource.name)
        }.toMap()

        request.artifacts
                .map { artifact ->
                    artifact.toBuilder()
                            .externalId(artifact.externalId
                                    .toBuilder()
                                    .resourceId(when (artifact.externalId.resourceId.type) {
                                        ARTIFACTS_RESOURCE -> artifactsResourceIds[artifact.externalId.resourceId.id]
                                        OPEN_SHIFT_RESOURCE -> openShiftResourceIds[artifact.externalId.resourceId.id]
                                        else -> throw PlatformException()
                                    })
                                    .build())
                            .build()
                }
                .map(ExternalArtifactRepository::putExternalArtifact)
    }

    fun deleteProject(id: Long): Project {
        deleteProjectModules(id)
        deleteProjectAssemblies(id)
        return ProjectRepository.deleteProject(id).deleted()
    }
}
