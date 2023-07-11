package ru.art.platform.service

import reactor.core.publisher.Flux
import ru.art.platform.agent.loader.createProjectLoadTestRunner
import ru.art.platform.api.model.load.LoadTest
import ru.art.platform.api.model.load.LoadTestScenario
import ru.art.platform.api.model.request.LoadTestRequest
import ru.art.platform.api.model.request.LoadTestScenarioRequest
import ru.art.platform.broker.added
import ru.art.platform.broker.deleted
import ru.art.platform.broker.loadTestConsumer
import ru.art.platform.broker.updated
import ru.art.platform.common.broker.PlatformEvent
import ru.art.platform.common.constants.States.*
import ru.art.platform.factory.LoadTestFactory.createLoadTest
import ru.art.platform.factory.LoadTestFactory.createLoadTestScenario
import ru.art.platform.repository.LoadTestingRepository
import ru.art.platform.repository.LoadTestingRepository.getLoadTests
import ru.art.platform.repository.LoadTestingRepository.putLoadTest
import ru.art.platform.repository.LoadTestingRepository.putLoadTestScenario
import ru.art.platform.repository.ProjectRepository.getProject
import ru.art.platform.service.ProjectLoadTestingService.cancelLoadTesting
import ru.art.platform.service.ProjectLoadTestingService.startLoadTesting
import ru.art.task.deferred.executor.SchedulerModuleActions.asynchronous
import java.time.Instant.now

object LoadTestingService {
    fun startLoadTest(request: LoadTestRequest): LoadTest {
        val loadTest = putLoadTest(createLoadTest(request)).added()
        startLoadTesting(loadTest, createProjectLoadTestRunner(request.resourceId, loadTest, getProject(loadTest.projectId)))
        return loadTest
    }

    fun cancelLoadTest(id: Long): LoadTest {
        val currentTest = getLoadTest(id)
        if (!currentTest.isRunning) {
            return currentTest
        }
        val newTest = putLoadTest(currentTest
                .toBuilder()
                .state(LOAD_TEST_CANCELED_STATE)
                .endTimeStamp(now().epochSecond)
                .build())
                .updated()
        if (currentTest.state != LOAD_TEST_RUNNING_STATE && currentTest.state != LOAD_TEST_STARTED_ON_RESOURCE_STATE) {
            return newTest
        }
        asynchronous {
            val runner = createProjectLoadTestRunner(newTest.resourceId, newTest, getProject(newTest.projectId))
            cancelLoadTesting(newTest, runner)
        }
        return newTest
    }

    fun getLoadTest(id: Long) = LoadTestingRepository.getLoadTest(id)

    fun saveLoadTestScenario(request: LoadTestScenarioRequest) = putLoadTestScenario(createLoadTestScenario(request))

    fun updateLoadTestScenario(request: LoadTestScenario) = putLoadTestScenario(request)

    fun getLoadTestScenario(request: Long) = LoadTestingRepository.getLoadTestScenario(request)

    fun getProjectsLoadTestScenarios(request: Long) = LoadTestingRepository.getProjectsLoadTestScenarios(request)

    fun getProjectsLoadTests(request: Long) = LoadTestingRepository.getProjectsLoadTests(request).sortedByDescending { test -> test.startTimeStamp }

    fun deleteLoadTestScenario(id: Long): LoadTestScenario {
        getLoadTests().filter { test -> test.scenarioId == id }.forEach { test -> deleteLoadTest(test.id) }
        return LoadTestingRepository.deleteLoadTestScenario(id)
    }

    fun subscribeOnLoadTest(): Flux<PlatformEvent> = loadTestConsumer()

    fun deleteLoadTest(id: Long): LoadTest = LoadTestingRepository.deleteLoadTest(id).deleted()
}