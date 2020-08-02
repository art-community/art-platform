package ru.art.platform.agent.dsl

import ru.art.core.colorizer.AnsiColorizer.success
import ru.art.platform.api.model.assembly.ArtifactConfiguration
import ru.art.platform.api.model.assembly.AssembledArtifact
import ru.art.platform.api.model.external.ExternalIdentifier
import ru.art.platform.api.model.resource.ResourceIdentifier
import ru.art.platform.common.constants.Resources.ARTIFACTS_RESOURCE
import ru.art.platform.common.constants.Resources.OPEN_SHIFT_RESOURCE
import ru.art.platform.common.constants.Technologies.DOCKER
import ru.art.platform.common.emitter.Emitter
import java.nio.file.Path
import java.util.concurrent.CopyOnWriteArrayList


data class ArtifactsUploadResult(val artifacts: List<AssembledArtifact>)
data class UploadingArtifact(val name: String, val version: String, val localPath: Path, val configuration: ArtifactConfiguration)

class ArtifactsUploader(private var uploadingArtifacts: MutableList<UploadingArtifact>) {
    private var dockerUploaders = mutableListOf<(UploadingArtifact, ResourceIdentifier) -> DockerImageUploader>()
    private var emitter: Emitter<String>? = null

    fun toDockerRegistry(uploader: (UploadingArtifact, ResourceIdentifier) -> DockerImageUploader): ArtifactsUploader {
        dockerUploaders.add(uploader)
        return this
    }

    fun emitter(emitter: Emitter<String>): ArtifactsUploader {
        this.emitter = emitter
        return this
    }

    fun artifacts(artifacts: List<UploadingArtifact>): ArtifactsUploader {
        this.uploadingArtifacts = artifacts.toMutableList()
        return this
    }

    fun artifact(artifact: UploadingArtifact): ArtifactsUploader {
        this.uploadingArtifacts.add(artifact)
        return this
    }

    fun upload(then: ArtifactsUploadResult.() -> Unit): ArtifactsUploader {
        if (uploadingArtifacts.isEmpty()) {
            emitter?.emit("No artifacts to upload")
            return this
        }
        emitter?.emit("Starting upload artifacts")
        val uploadedArtifacts = CopyOnWriteArrayList<AssembledArtifact>()
        uploadingArtifacts.forEach { artifact -> uploadArtifact(artifact, uploadedArtifacts) }
        emitter?.emit(success("Uploading artifacts finished"))
        then(ArtifactsUploadResult(uploadedArtifacts))
        return this
    }

    private fun uploadArtifact(artifact: UploadingArtifact, collector: CopyOnWriteArrayList<AssembledArtifact>) {
        emitter?.emit(success("[${artifact.name}-${artifact.version}]: Upload started"))
        artifact.configuration.archives.parallelStream().filter { archive -> !archive.archiveTechnology.isNullOrBlank() }.forEach { archive ->
            emitter?.emit("[${artifact.name}-${artifact.version}]: Uploading Docker image to resource ${archive.resourceId.name}")
            when (archive.archiveTechnology) {
                DOCKER -> when (archive.resourceId.type) {
                    ARTIFACTS_RESOURCE, OPEN_SHIFT_RESOURCE -> dockerUploaders.map { uploader ->
                        uploader(artifact, archive.resourceId).apply {
                            emitter?.let(::emitter)
                            upload { collector.add(createAssembledArtifact(this, artifact, archive.resourceId)) }
                        }
                    }
                }
            }
        }
        emitter?.emit(success("[${artifact.name}-${artifact.version}]: Upload finished"))
    }

    private fun createAssembledArtifact(id: String, artifact: UploadingArtifact, resourceId: ResourceIdentifier): AssembledArtifact =
            AssembledArtifact.builder()
                    .name(artifact.name)
                    .version(artifact.version)
                    .externalId(ExternalIdentifier.builder()
                            .id(id)
                            .resourceId(resourceId)
                            .build())
                    .build()
}
