package ru.art.platform.agent.extension

import ru.art.core.constants.StringConstants.EMPTY_STRING
import ru.art.platform.agent.dsl.*
import ru.art.platform.api.model.assembly.AssemblyEvent
import ru.art.platform.api.model.load.LoadTestEvent
import ru.art.platform.api.model.project.ProjectEvent
import ru.art.platform.common.emitter.Emitter
import ru.art.platform.common.emitter.ReactiveEmitter
import ru.art.platform.common.emitter.emitter
import ru.art.platform.common.extensions.formatLogRecord
import java.nio.file.Path
import java.nio.file.Paths.get

typealias AssemblyEmitter = ReactiveEmitter<AssemblyEvent.AssemblyEventBuilder, AssemblyEvent>
typealias ProjectEmitter = ReactiveEmitter<ProjectEvent.ProjectEventBuilder, ProjectEvent>
typealias LoadTestEmitter = ReactiveEmitter<LoadTestEvent.LoadTestEventBuilder, LoadTestEvent>

fun AssemblyEmitter.toAssemblyEmitter(): Emitter<String> = emitter(
        { record -> emit { logRecord(record) } },
        { error -> emitError(error) },
        { complete() }
)

fun LoadTestEmitter.toLoadTestEmitter(): Emitter<String> = emitter(
        { record -> emit { logRecord(record) } },
        { error -> emitError(error) },
        { complete() }
)

fun AssemblyEmitter.gradleForAssembly(projectPath: Path = get(EMPTY_STRING), builder: GradleBuilder.() -> GradleBuilder): GradleBuilder =
        builder(GradleBuilder(projectPath).emitter(toAssemblyEmitter()))

fun LoadTestEmitter.gradleForLoadTest(projectPath: Path = get(EMPTY_STRING), builder: GradleBuilder.() -> GradleBuilder): GradleBuilder =
        builder(GradleBuilder(projectPath).emitter(toLoadTestEmitter()))

fun AssemblyEmitter.dockerFile(path: Path = get(EMPTY_STRING), generator: DockerFileGenerator.() -> DockerFileGenerator): DockerFileGenerator =
        generator(DockerFileGenerator(path).emitter(toAssemblyEmitter()))

fun AssemblyEmitter.uploader(uploadingArtifacts: List<UploadingArtifact>, uploader: ArtifactsUploader.() -> ArtifactsUploader): ArtifactsUploader =
        uploader(ArtifactsUploader(uploadingArtifacts.toMutableList()).emitter(toAssemblyEmitter()))

fun AssemblyEmitter.kaniko(contextPath: Path = get(EMPTY_STRING), executor: KanikoExecutor.() -> KanikoExecutor): KanikoExecutor =
        executor(KanikoExecutor(contextPath).emitter(toAssemblyEmitter()))

fun AssemblyEmitter.dockerImageUploader(uploader: DockerImageUploader.() -> DockerImageUploader): DockerImageUploader =
        uploader(DockerImageUploader().emitter(toAssemblyEmitter()))
