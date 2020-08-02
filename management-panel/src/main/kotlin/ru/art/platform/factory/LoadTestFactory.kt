package ru.art.platform.factory

import ru.art.platform.api.model.load.*
import ru.art.platform.api.model.log.*
import ru.art.platform.api.model.request.*
import ru.art.platform.repository.*

object LoadTestFactory {
    fun createLoadTest(request: LoadTestRequest): LoadTest = LoadTest.builder()
            .scenarioId(request.scenarioId)
            .logId(LogRepository.putLog(Log.builder().build()).id)
            .environmentVariables(request.environmentVariables)
            .projectId(request.projectId)
            .resourceId(request.resourceId)
            .version(request.version)
            .build()

    fun createLoadTestScenario(request: LoadTestScenarioRequest): LoadTestScenario = LoadTestScenario.builder()
            .name(request.name)
            .projectId(request.projectId)
            .defaultResourceId(request.defaultResourceId)
            .launchTechnology(request.launchTechnology)
            .reportTechnology(request.reportTechnology)
            .gradleConfiguration(request.gradleConfiguration)
            .build()
}
