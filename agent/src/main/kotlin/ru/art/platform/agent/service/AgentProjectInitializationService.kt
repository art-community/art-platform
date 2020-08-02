package ru.art.platform.agent.service

import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers.elastic
import ru.art.core.factory.CollectionsFactory.concurrentHashMap
import ru.art.core.factory.CollectionsFactory.setOf
import ru.art.platform.agent.constants.TechnologyFiles.BUILD_GRADLE_FILE
import ru.art.platform.agent.constants.TechnologyFiles.BUILD_GRADLE_KTS_FILE
import ru.art.platform.agent.constants.TechnologyFiles.DOCKER_FILE
import ru.art.platform.agent.constants.TechnologyFiles.GROOVY_FILE_EXTENSION
import ru.art.platform.agent.constants.TechnologyFiles.JAVA_FILE_EXTENSION
import ru.art.platform.agent.constants.TechnologyFiles.JS_FILE_EXTENSION
import ru.art.platform.agent.constants.TechnologyFiles.KOTLIN_FILE_EXTENSION
import ru.art.platform.agent.constants.TechnologyFiles.PACKAGE_JSON_FILE
import ru.art.platform.agent.constants.TechnologyFiles.POM_XML_FILE
import ru.art.platform.agent.constants.TechnologyFiles.TS_FILE_EXTENSION
import ru.art.platform.agent.constants.TechnologyFiles.YARN_LOCK_FILE
import ru.art.platform.agent.extension.ProjectEmitter
import ru.art.platform.agent.extension.listFiles
import ru.art.platform.agent.service.AgentGradleService.findGradleModules
import ru.art.platform.api.model.project.ProjectArtifact
import ru.art.platform.api.model.project.ProjectEvent
import ru.art.platform.api.model.project.ProjectEvent.ProjectEventBuilder
import ru.art.platform.api.model.project.ProjectVersion
import ru.art.platform.api.model.request.AgentProjectInitializationRequest
import ru.art.platform.common.constants.Technologies.*
import ru.art.platform.common.emitter.reactiveEmitter
import ru.art.platform.common.extensions.normalizeNameToId
import ru.art.platform.git.service.GitService.checkoutRemoteReference
import ru.art.platform.git.service.GitService.fetchProject
import ru.art.platform.git.service.GitService.getRemoteTagsAndBranches
import java.nio.file.Path

data class ComputedArtifact(
        val name: String,
        val path: String,
        val technologies: Set<String>
)

object AgentProjectInitializationService {
    fun initializeProject(request: AgentProjectInitializationRequest): Flux<ProjectEvent> =
            reactiveEmitter<ProjectEvent, ProjectEventBuilder>(ProjectEvent.builder()
                    .project(request.project)
                    .build())
                    .from { build() }
                    .to { toBuilder() }
                    .defer { processProjectInitialization(request) }
                    .subscribeOn(elastic(), false)

    private fun ProjectEmitter.processProjectInitialization(request: AgentProjectInitializationRequest) {
        with(request) {
            val projectPath = fetchProject(gitResource, project.externalId.id)

            val technologies = loadTechnologies(projectPath)

            emit { project(current().project.toBuilder().clearTechnologies().technologies(technologies).build()) }

            val references = getRemoteTagsAndBranches(gitResource, project.externalId.id)

            val versions = references
                    .map { reference ->
                        reference to ProjectVersion.builder()
                                .reference(reference)
                                .version(reference.normalizeNameToId())
                                .build()
                    }
                    .toMap()


            emit {
                project(current().project.toBuilder().clearVersions().versions(versions.values).build())
            }

            val artifacts = mutableMapOf<String, ProjectArtifact>()
            references.forEach { reference ->
                checkoutRemoteReference(gitResource, project.externalId.id, reference)
                computeArtifacts(technologies, projectPath).forEach { computedArtifact ->
                    artifacts[computedArtifact.name]
                            ?.let { artifact -> artifacts[computedArtifact.name] = artifact.toBuilder().version(versions[reference]).build() }
                            ?: artifacts.put(computedArtifact.name, ProjectArtifact.builder()
                                    .name(computedArtifact.name)
                                    .path(computedArtifact.path)
                                    .technologies(computedArtifact.technologies)
                                    .version(versions[reference])
                                    .build())
                }
            }

            emit {
                project(current().project.toBuilder().clearArtifacts().artifacts(artifacts.values).build())
            }

            complete()
        }
    }

    private fun loadTechnologies(projectPath: Path): List<String> = projectPath.listFiles()
            .filter { file ->
                val fileNameFound = file.name in arrayOf(PACKAGE_JSON_FILE,
                        BUILD_GRADLE_FILE,
                        POM_XML_FILE,
                        BUILD_GRADLE_KTS_FILE,
                        YARN_LOCK_FILE,
                        DOCKER_FILE
                )
                val fileExtensionFound = arrayOf(JAVA_FILE_EXTENSION,
                        KOTLIN_FILE_EXTENSION,
                        GROOVY_FILE_EXTENSION,
                        JS_FILE_EXTENSION,
                        TS_FILE_EXTENSION
                ).any { extension -> file.name.endsWith(extension) }
                fileNameFound || fileExtensionFound
            }
            .map { file ->
                when (file.name) {
                    PACKAGE_JSON_FILE -> NPM
                    BUILD_GRADLE_FILE -> GRADLE
                    POM_XML_FILE -> MAVEN
                    BUILD_GRADLE_KTS_FILE -> GRADLE
                    YARN_LOCK_FILE -> YARN
                    DOCKER_FILE -> DOCKER
                    else -> when {
                        file.name.endsWith(JAVA_FILE_EXTENSION) -> JAVA
                        file.name.endsWith(KOTLIN_FILE_EXTENSION) -> KOTLIN
                        file.name.endsWith(GROOVY_FILE_EXTENSION) -> GROOVY
                        file.name.endsWith(JS_FILE_EXTENSION) -> JS
                        file.name.endsWith(TS_FILE_EXTENSION) -> TS
                        else -> throw IllegalStateException()
                    }
                }
            }
            .toSet()
            .toList()

    private fun computeArtifacts(technologies: List<String>, projectPath: Path): Set<ComputedArtifact> {
        val artifacts = mutableSetOf<ComputedArtifact>()
        when {
            technologies.contains(GRADLE) -> {
                findGradleModules(projectPath).forEach { module ->
                    artifacts.add(ComputedArtifact(module.name, module.path.toFile().path, setOf(GRADLE)))
                }
            }
        }
        return artifacts
    }
}

