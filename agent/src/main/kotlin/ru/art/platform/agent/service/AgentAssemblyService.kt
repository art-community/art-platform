package ru.art.platform.agent.service

import reactor.core.publisher.*
import reactor.core.scheduler.Schedulers.*
import ru.art.core.colorizer.AnsiColorizer.*
import ru.art.platform.agent.builder.AgentProjectBuilder.buildByGradle
import ru.art.platform.api.model.assembly.*
import ru.art.platform.api.model.assembly.AssemblyEvent.*
import ru.art.platform.api.model.request.*
import ru.art.platform.common.constants.Technologies.*
import ru.art.platform.common.emitter.*
import ru.art.platform.common.extensions.formatLogRecord
import ru.art.platform.git.service.GitService.fetchRemoteReference
import java.time.Instant.*


object AgentAssemblyService {
    fun buildProject(request: AgentProjectBuildRequest): Flux<AssemblyEvent> = reactiveEmitter<AssemblyEvent, AssemblyEventBuilder>(builder()
            .assembly(request.assembly)
            .build())
            .to { toBuilder() }
            .from { build() }
            .defer {
                val projectPath = fetchRemoteReference(request.gitResource, request.projectId.id, current().assembly.version.reference)
                emit { logRecord("Starting assembly") }

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
