package ru.art.platform.agent.service

import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers.single
import ru.art.core.colorizer.AnsiColorizer.success
import ru.art.platform.agent.builder.AgentProjectBuilder.buildByGradle
import ru.art.platform.api.model.assembly.AssemblyEvent
import ru.art.platform.api.model.assembly.AssemblyEvent.AssemblyEventBuilder
import ru.art.platform.api.model.assembly.AssemblyEvent.builder
import ru.art.platform.api.model.request.AgentProjectBuildRequest
import ru.art.platform.common.constants.Technologies.GRADLE
import ru.art.platform.common.emitter.reactiveEmitter
import ru.art.platform.git.service.GitService.fetchRemoteReference
import java.time.Instant.now


object AgentAssemblyService {
    fun buildProject(request: AgentProjectBuildRequest): Flux<AssemblyEvent> = reactiveEmitter<AssemblyEvent, AssemblyEventBuilder>(builder()
            .assembly(request.assembly)
            .build())
            .to { toBuilder() }
            .from { build() }
            .defer {
                emit { logRecord("Starting assembly") }
                emit { logRecord("Fetching project...") }
                val projectPath = fetchRemoteReference(request.gitResource, request.projectId.id, current().assembly.version.reference)
                emit { logRecord("Project fetched") }

                when (request.assemblyConfiguration.technology) {
                    GRADLE -> buildByGradle(request, projectPath)
                }

                emitWithCompletion {
                    assembly(current().assembly.toBuilder().endTimeStamp(now().epochSecond).build())
                    logRecord(success("Assembly completed successfully"))
                }
            }
            .subscribeOn(single(), false)
}
