package ru.art.platform.agent.dsl

import ru.art.core.constants.StringConstants.EMPTY_STRING
import ru.art.platform.api.model.resource.OpenShiftResource
import java.nio.file.Path
import java.nio.file.Paths


object AgentDsl {
    fun uploadArtifacts(uploadingArtifacts: List<UploadingArtifact> = emptyList(), uploader: ArtifactsUploader.() -> ArtifactsUploader) =
            uploader(ArtifactsUploader(uploadingArtifacts.toMutableList()))

    fun dockerFile(directory: Path = Paths.get(EMPTY_STRING), generator: DockerFileGenerator.() -> DockerFileGenerator) =
            generator(DockerFileGenerator(directory)).generate()

    fun gradle(projectPath: Path = Paths.get(EMPTY_STRING), builder: GradleBuilder.() -> GradleBuilder) =
            builder(GradleBuilder(projectPath))

    fun kaniko(contextPath: Path = Paths.get(EMPTY_STRING), executor: KanikoExecutor.() -> KanikoExecutor) =
            executor(KanikoExecutor(contextPath))

    fun uploadImage(uploader: DockerImageUploader.() -> DockerImageUploader) = uploader(DockerImageUploader())

    fun installModule(resource: OpenShiftResource, name: String, image: String, installer: OpenShiftInstaller.() -> OpenShiftInstaller) =
            installer(OpenShiftInstaller(resource, name, image))

    fun updateModule(resource: OpenShiftResource, name: String, image: String, updater: OpenShiftUpdater.() -> OpenShiftUpdater) =
            updater(OpenShiftUpdater(resource, name, image))

    fun restartModule(resource: OpenShiftResource, projectName: String, name: String, newCount: Int) =
            restartDeploymentOnOpenShift(resource, projectName, name, newCount)

    fun deleteModule(resource: OpenShiftResource, projectName: String, name: String) =
            deleteDeploymentOnOpenShift(resource, projectName, name)

    fun stopModule(resource: OpenShiftResource, projectName: String, name: String) =
            stopDeploymentOnOpenShift(resource, projectName, name)
}
