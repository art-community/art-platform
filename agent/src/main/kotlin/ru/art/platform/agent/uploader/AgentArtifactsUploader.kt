package ru.art.platform.agent.uploader

import ru.art.platform.agent.constants.ModuleImageConstants.MODULE_PATH
import ru.art.platform.agent.dsl.DockerImageUploader
import ru.art.platform.agent.dsl.UploadingArtifact
import ru.art.platform.agent.extension.*
import ru.art.platform.api.model.request.AgentProjectBuildRequest
import ru.art.platform.api.model.resource.ResourceIdentifier
import ru.art.platform.common.constants.Resources.ARTIFACTS_RESOURCE
import ru.art.platform.common.constants.Resources.OPEN_SHIFT_RESOURCE
import ru.art.platform.common.constants.Technologies.JVM
import ru.art.platform.common.constants.Technologies.NGINX
import java.nio.file.Files.createDirectories
import java.nio.file.Paths.get

object AgentArtifactsUploader {
    fun AssemblyEmitter.dockerImage(request: AgentProjectBuildRequest, resourceId: ResourceIdentifier, artifact: UploadingArtifact): DockerImageUploader {
        val imageWorkingDirectory = "$MODULE_PATH/${request.projectId.id}/${artifact.name}"
        return dockerImageUploader {
            when (resourceId.type) {
                ARTIFACTS_RESOURCE -> {
                    val resource = request.artifactsResources.findArtifactsResource(resourceId)
                    pushToRegistry(resource.url, resource.userName, resource.password)
                }
                OPEN_SHIFT_RESOURCE -> {
                    val resource = request.openShiftResources.findOpenShiftResource(resourceId)
                    if (resource.id == resourceId.id) {
                        pushToLocalOpenShiftRegistry(resource.privateRegistryUrl)
                    }
                }
            }

            when (request.assembly.resourceId.type) {
                OPEN_SHIFT_RESOURCE -> {
                    val resource = request.openShiftResources.findOpenShiftResource(resourceId)
                    useLocalOpenShiftRegistry(resource.privateRegistryUrl)
                }
            }

            imageName(artifact.name)
            imageVersion(artifact.version)
            imageWorkingDirectory(createDirectories(get(imageWorkingDirectory)))
            imageContextPath(artifact.localPath)
            projectName(request.projectId.id)

            dockerFile(artifact.localPath) {
                val archive = artifact.configuration.archives.findArchiveConfiguration(resourceId)
                when (archive.dockerConfiguration?.containerTechnology?.toLowerCase()) {
                    JVM -> jarImage {
                        cache()
                        name(artifact.name)
                        version(artifact.version)
                        jdkImage(archive.dockerConfiguration.image)
                        workingDirectory(imageWorkingDirectory)
                        localPaths(archive.dockerConfiguration.sourcePaths)
                    }
                    NGINX -> nginxImage {
                        name(artifact.name)
                        workingDirectory(imageWorkingDirectory)
                        version(artifact.version)
                        nginxImage(archive.dockerConfiguration.image)
                        staticContentPaths(archive.dockerConfiguration.sourcePaths)
                        workingDirectory(imageWorkingDirectory)
                    }
                    else -> this
                }
            }
        }
    }
}
