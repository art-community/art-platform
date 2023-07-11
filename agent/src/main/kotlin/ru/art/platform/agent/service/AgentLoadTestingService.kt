package ru.art.platform.agent.service

import org.zeroturnaround.zip.ZipUtil.pack
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers.single
import ru.art.core.colorizer.AnsiColorizer.success
import ru.art.platform.agent.constants.GatlingConstants.GATLING_REPORTS_PATH
import ru.art.platform.agent.extension.gradleForLoadTest
import ru.art.platform.api.model.load.LoadTestEvent
import ru.art.platform.api.model.load.LoadTestEvent.LoadTestEventBuilder
import ru.art.platform.api.model.load.LoadTestEvent.builder
import ru.art.platform.api.model.request.AgentLoadTestRequest
import ru.art.platform.common.emitter.reactiveEmitter
import ru.art.platform.common.extensions.formatLogRecord
import ru.art.platform.git.service.GitService.fetchRemoteReference
import java.io.ByteArrayOutputStream
import java.time.Instant.now

object AgentLoadTestingService {
    fun startLoadTesting(request: AgentLoadTestRequest): Flux<LoadTestEvent> = reactiveEmitter<LoadTestEvent, LoadTestEventBuilder>(builder().loadTest(request.loadTest).build())
            .from { build() }
            .to { toBuilder() }
            .defer {
                val projectPath = fetchRemoteReference(request.gitResource, request.projectId.id, current().loadTest.version.reference)
                emit { logRecord("Starting load test") }
                gradleForLoadTest(projectPath) {
                    configure(request.loadTestScenario.gradleConfiguration)
                    build {
                        emitWithCompletion {
                            val buffer = ByteArrayOutputStream()
                            projectPath
                                    .toFile()
                                    .walkTopDown()
                                    .find { file -> file.absolutePath.contains(GATLING_REPORTS_PATH) }
                                    ?.let { directory -> pack(directory, buffer) }
                                    ?.let { reportArchiveBytes(buffer.toByteArray()) }
                            loadTest(current().loadTest.toBuilder().endTimeStamp(now().epochSecond).build())
                            logRecord(success("Load test completed successfully"))
                        }
                    }
                }
            }
            .subscribeOn(single(), false)
}
