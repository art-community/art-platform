package ru.art.platform.agent.service

import ru.art.core.constants.StringConstants.SLASH
import ru.art.platform.api.model.project.ProjectChanges
import ru.art.platform.api.model.request.AgentProjectChangesRequest
import ru.art.platform.git.service.GitService.checkoutRemoteReference
import ru.art.platform.git.service.GitService.difference
import ru.art.platform.git.service.GitService.fetchProject
import ru.art.platform.git.service.changes
import java.nio.file.Path
import java.util.*

object AgentProjectChangesService {
    fun computeProjectChanges(request: AgentProjectChangesRequest): ProjectChanges = with(request) {
        val projectPath = fetchProject(gitResource, project.externalId.id)
        checkoutRemoteReference(gitResource, project.externalId.id, reference)
        loadReferenceChanges(projectPath, request)
    }

    private fun loadReferenceChanges(projectPath: Path, request: AgentProjectChangesRequest): ProjectChanges {
        val artifacts = request.project.artifacts
                .filter { artifact -> artifact.versions.any { version -> version.reference == request.reference } }
                .map { artifact -> artifact.path to artifact }
                .toMap()
        val changes = difference(projectPath.toAbsolutePath().toString(), request.fromHash, request.toHash).changes()
        val changedArtifacts = changes
                .added
                .map { filePath -> artifacts[filePath.substringBefore(SLASH)] }

                .plus(changes.modified.map { filePath -> artifacts[filePath.substringBefore(SLASH)] })

                .plus(changes.renamed.values.map { filePath -> artifacts[filePath.substringBefore(SLASH)] })

                .plus(changes.copied
                        .flatMap { entry -> setOf(entry.key, entry.value) }
                        .map { filePath -> artifacts[filePath.substringBefore(SLASH)] })

        return ProjectChanges.builder()
                .changedArtifacts(changedArtifacts.filter(Objects::nonNull).map { artifact -> artifact!! }.distinctBy { artifact -> artifact.name })
                .build()
    }
}

