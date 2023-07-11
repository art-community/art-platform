package ru.art.platform.agent.builder

import ru.art.core.colorizer.AnsiColorizer.success
import ru.art.platform.agent.dsl.UploadingArtifact
import ru.art.platform.agent.extension.AssemblyEmitter
import ru.art.platform.agent.extension.findArtifactConfiguration
import ru.art.platform.agent.extension.gradleForAssembly
import ru.art.platform.agent.extension.uploader
import ru.art.platform.agent.uploader.AgentArtifactsUploader.dockerImage
import ru.art.platform.api.model.assembly.AssembledArtifact
import ru.art.platform.api.model.request.AgentProjectBuildRequest
import java.nio.file.Path

object AgentProjectBuilder {
    fun AssemblyEmitter.buildByGradle(request: AgentProjectBuildRequest, projectPath: Path) {
        gradleForAssembly(projectPath) {
            clean()
            exitIfError()
            artifacts(request.artifactConfigurations.map { configuration -> configuration.artifact.name to configuration.gradleConfiguration }.toMap())
            configure(current().assembly, request.assemblyConfiguration, request.cacheConfiguration)
            build {
                val uploadingArtifacts = artifacts(current().assembly.version.version)
                        .filter { artifact -> request.artifactConfigurations.any { configuration -> configuration.artifact.name == artifact.name } }
                        .map { artifact ->
                            UploadingArtifact(
                                    name = artifact.name,
                                    version = artifact.version,
                                    localPath = artifact.localPath,
                                    configuration = request.artifactConfigurations.findArtifactConfiguration(artifact.name))
                        }
                uploader(uploadingArtifacts) {
                    toDockerRegistry { artifact, resourceId -> dockerImage(request, resourceId, artifact) }
                    upload { artifacts.forEach { artifact -> emitUploadedArtifact(artifact) } }
                }
            }
        }
    }

    private fun AssemblyEmitter.emitUploadedArtifact(artifact: AssembledArtifact) {
        emit {
            assembly(current().assembly.toBuilder().artifact(artifact).build())
            logRecord(success("Added artifact with an external id: ${artifact.externalId.id}"))
        }
    }
}
