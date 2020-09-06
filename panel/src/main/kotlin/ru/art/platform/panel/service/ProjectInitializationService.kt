package ru.art.platform.panel.service

import ru.art.logging.LoggingModule.loggingModule
import ru.art.platform.panel.agent.initialzier.ProjectInitializer
import ru.art.platform.api.model.project.Project
import ru.art.platform.api.model.project.ProjectEvent
import ru.art.platform.api.model.request.AgentProjectInitializationRequest
import ru.art.platform.panel.broker.added
import ru.art.platform.panel.broker.updated
import ru.art.platform.panel.client.connectToAgent
import ru.art.platform.common.constants.States.*
import ru.art.platform.panel.repository.GitResourceRepository.getGitResource
import ru.art.platform.panel.repository.ProjectRepository.getProject
import ru.art.platform.panel.repository.ProjectRepository.putProject
import ru.art.platform.panel.repository.ProjectRepository.tryGetProject
import ru.art.task.deferred.executor.SchedulerModuleActions.asynchronous

object ProjectInitializationService {
    fun initializeProject(project: Project, initializer: ProjectInitializer) {
        asynchronous {
            with(getProject(project.added().id)) {
                try {
                    val request = AgentProjectInitializationRequest.builder()
                            .gitResource(getGitResource(gitResourceId.id))
                            .project(this)
                            .build()
                    connectToAgent(initializer.startAgent())
                            .initializeProject(request)
                            .doOnError { error -> handleProjectInitializationError(error) }
                            .doOnComplete { completeProjectInitialization() }
                            .subscribe(ProjectInitializationService::updateProject)
                } catch (error: Throwable) {
                    handleProjectInitializationError(error)
                }
            }
        }
    }

    fun reloadProject(project: Project, initializer: ProjectInitializer) {
        asynchronous {
            with(getProject(project.id)) {
                try {
                    val request = AgentProjectInitializationRequest.builder()
                            .gitResource(getGitResource(gitResourceId.id))
                            .project(project.toBuilder().state(PROJECT_RELOADING_STATE).build().apply { putProject(this).updated() })
                            .build()
                    connectToAgent(initializer.startAgent())
                            .initializeProject(request)
                            .doOnError { error -> handleProjectInitializationError(error) }
                            .doOnComplete { completeProjectInitialization() }
                            .subscribe(ProjectInitializationService::updateProject)
                } catch (error: Throwable) {
                    handleProjectInitializationError(error)
                }
            }
        }
    }

    private fun updateProject(event: ProjectEvent) {
        tryGetProject(event.project.id).ifPresent {
            putProject(event.project).updated()
        }
    }

    private fun Project.handleProjectInitializationError(error: Throwable) {
        tryGetProject(id).ifPresent { project ->
            loggingModule().getLogger(ProjectInitializationService::class.java).error(error.message, error)
            putProject(project.toBuilder().state(PROJECT_INITIALIZATION_FAILED_STATE).build()).updated()
        }
    }

    private fun Project.completeProjectInitialization() {
        tryGetProject(id).ifPresent { project ->
            putProject(project.toBuilder().state(PROJECT_INITIALIZED_STATE).build()).updated()
        }
    }
}
