package ru.art.platform.service

import com.google.common.base.Throwables.getStackTraceAsString
import org.reactivestreams.Subscription
import reactor.core.Disposable
import ru.art.core.colorizer.AnsiColorizer.error
import ru.art.logging.LoggingModule.loggingModule
import ru.art.platform.agent.loader.ProjectLoadTestRunner
import ru.art.platform.api.model.file.PlatformFileIdentifier
import ru.art.platform.api.model.load.LoadTest
import ru.art.platform.api.model.load.LoadTestEvent
import ru.art.platform.api.model.request.AgentLoadTestRequest
import ru.art.platform.broker.updated
import ru.art.platform.client.AgentClient
import ru.art.platform.client.connectToAgent
import ru.art.platform.client.removeAgent
import ru.art.platform.common.constants.States.*
import ru.art.platform.constants.RsocketConstants.AGENT_STREAM_CHUNKS_COUNT
import ru.art.platform.extensions.ChunkedRequester
import ru.art.platform.extensions.chunked
import ru.art.platform.repository.GitResourceRepository.getGitResource
import ru.art.platform.repository.LoadTestingRepository.getLoadTest
import ru.art.platform.repository.LoadTestingRepository.putLoadTest
import ru.art.platform.repository.LoadTestingRepository.tryGetLoadTest
import ru.art.platform.repository.LogRepository.getLog
import ru.art.platform.repository.LogRepository.putLog
import ru.art.platform.repository.ProjectRepository.getProject
import ru.art.platform.service.FileService.putFile
import ru.art.platform.service.LoadTestingService.getLoadTestScenario
import ru.art.task.deferred.executor.SchedulerModuleActions.asynchronous
import java.time.Instant.now
import java.util.concurrent.ConcurrentHashMap

object ProjectLoadTestingService {
    private val subscriptions = ConcurrentHashMap<Long, Subscription>()
    private val agents = ConcurrentHashMap<Long, AgentClient>()
    private val disposables = ConcurrentHashMap<Long, Disposable>()
    private val requester = ChunkedRequester(AGENT_STREAM_CHUNKS_COUNT)

    fun startLoadTesting(test: LoadTest, testRunner: ProjectLoadTestRunner) {
        with(test) {
            asynchronous {
                try {
                    if (getLoadTest(id).isCanceled) {
                        testRunner.stopAgent()
                        return@asynchronous
                    }
                    val configuration = testRunner.startAgent()
                    if (getLoadTest(id).isCanceled) {
                        testRunner.stopAgent()
                        return@asynchronous
                    }
                    val request = AgentLoadTestRequest
                            .builder()
                            .projectId(getProject(projectId).externalId)
                            .loadTest(toBuilder().state(LOAD_TEST_STARTED_ON_RESOURCE_STATE).build().apply { updateLoadTest(this) })
                            .loadTestScenario(getLoadTestScenario(test.scenarioId))
                            .gitResource(getGitResource(getProject(projectId).gitResourceId.id))
                            .build()
                    disposables[id] = connectToAgent(configuration).apply { agents[id] = this }
                            .startLoadTest(request)
                            .chunked(requester)
                            .doOnSubscribe { subscription -> subscriptions[id] = subscription }
                            .doOnError { error -> handleLoadTestError(error, testRunner) }
                            .doOnComplete { completeLoadTest(testRunner) }
                            .subscribe { event -> updateLoadTest(event) }
                } catch (error: Throwable) {
                    handleLoadTestError(error, testRunner)
                }
            }
        }
    }

    fun cancelLoadTesting(test: LoadTest, testRunner: ProjectLoadTestRunner) {
        destroyAgent(test, testRunner)
    }

    private fun LoadTest.updateLoadTest(event: LoadTestEvent) {
        tryGetLoadTest(id)
                .filter { test -> test.isRunning }
                .ifPresent { loadTest ->
                    putLog(getLog(logId).toBuilder().record(event.logRecord).build()).updated()
                    if (event.loadTest != loadTest) {
                        val loadTestBuilder = event.loadTest.toBuilder()
                        event.reportArchiveBytes?.takeIf { bytes -> bytes.isNotEmpty() }?.let { bytes ->
                            val file = putFile("report-${loadTest.id}.zip", bytes)
                            loadTestBuilder.reportArchiveName(PlatformFileIdentifier.builder().name(file.name).id(file.id).build())
                        }
                        putLoadTest(loadTestBuilder.state(LOAD_TEST_RUNNING_STATE).endTimeStamp(now().epochSecond).build()).updated()
                    }
                    requester.request()
                }
    }

    private fun updateLoadTest(loadTest: LoadTest) {
        tryGetLoadTest(loadTest.id)
                .filter { test -> test.isRunning }
                .ifPresent { putLoadTest(loadTest).updated() }
    }

    private fun LoadTest.handleLoadTestError(error: Throwable, testRunner: ProjectLoadTestRunner) {
        loggingModule().getLogger(ProjectLoadTestingService::class.java).error(error.message, error)
        tryGetLoadTest(id)
                .filter { test -> test.isRunning }
                .ifPresent { loadTest ->
                    putLoadTest(loadTest
                            .toBuilder()
                            .endTimeStamp(now().epochSecond)
                            .state(LOAD_TEST_FAILED_STATE)
                            .build())
                            .updated()
                    putLog(getLog(logId).toBuilder()
                            .record(error("Load test failed: ${error.message}\n${getStackTraceAsString(error)}"))
                            .build())
                            .updated()
                }
        asynchronous { destroyAgent(this, testRunner) }
    }

    private fun LoadTest.completeLoadTest(testRunner: ProjectLoadTestRunner) {
        tryGetLoadTest(id)
                .filter { test -> test.isRunning }
                .ifPresent { loadTest ->
                    getLog(logId).updated()
                    putLoadTest(loadTest.toBuilder()
                            .endTimeStamp(now().epochSecond)
                            .state(LOAD_TEST_DONE_STATE)
                            .build())
                            .updated()
                }
        asynchronous { destroyAgent(this, testRunner) }
    }

    private fun destroyAgent(test: LoadTest, runner: ProjectLoadTestRunner) {
        subscriptions[test.id]?.cancel()
        disposables[test.id]?.dispose()
        runner.stopAgent()
        agents[test.id]?.let(::removeAgent)
        agents.remove(test.id)
        disposables.remove(test.id)
        subscriptions.remove(test.id)
    }
}
