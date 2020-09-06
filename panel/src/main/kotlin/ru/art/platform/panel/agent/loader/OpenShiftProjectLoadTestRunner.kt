package ru.art.platform.panel.agent.loader

import ru.art.platform.api.model.load.LoadTest
import ru.art.platform.api.model.project.Project
import ru.art.platform.api.model.resource.OpenShiftResource
import ru.art.platform.common.extractor.extractHostName
import ru.art.platform.panel.configurator.OpenShiftAgentConfigurator.configure
import ru.art.platform.panel.constants.OpenShiftConstants.PLATFORM_LOAD_TEST_RUNNER
import ru.art.platform.open.shift.manager.startOpenShiftAgent
import ru.art.platform.open.shift.manager.stopOpenShiftAgent
import ru.art.rsocket.model.RsocketCommunicationTargetConfiguration
import ru.art.rsocket.model.RsocketCommunicationTargetConfiguration.rsocketCommunicationTarget

class OpenShiftProjectLoadTestRunner(private val resource: OpenShiftResource, private val loadTest: LoadTest, private val project: Project) : ProjectLoadTestRunner {
    override fun startAgent(): RsocketCommunicationTargetConfiguration = rsocketCommunicationTarget()
            .host(extractHostName(resource.apiUrl))
            .tcpPort(startOpenShiftAgent(resource, "$PLATFORM_LOAD_TEST_RUNNER-${loadTest.id}") {
                configure()
                loadTest.environmentVariables.forEach { variable -> environmentVariable(variable.name, variable.value) }
                projectName(project.externalId.id)
            }.rsocket)
            .build()

    override fun stopAgent() {
        stopOpenShiftAgent(resource, "$PLATFORM_LOAD_TEST_RUNNER-${loadTest.id}", project.externalId.id)
    }
}
