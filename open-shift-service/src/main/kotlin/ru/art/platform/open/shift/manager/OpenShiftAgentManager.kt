package ru.art.platform.open.shift.manager

import com.openshift.restclient.images.DockerImageURI
import ru.art.core.constants.StringConstants.EMPTY_STRING
import ru.art.core.network.selector.PortSelector.findAvailableTcpPort
import ru.art.platform.api.model.resource.OpenShiftResource
import ru.art.platform.common.constants.CommonConstants.*
import ru.art.platform.common.constants.ErrorCodes.PLATFORM_ERROR
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.common.extractor.extractHostName
import ru.art.platform.common.extractor.extractPath
import ru.art.platform.open.shift.constants.OpenShiftConstants.IMAGE_PULL_POLICY_ALWAYS
import ru.art.platform.open.shift.model.OpenShiftPodWaitingConfiguration
import ru.art.platform.open.shift.service.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

fun startOpenShiftAgent(resource: OpenShiftResource, agentName: String, manager: OpenShiftAgentManager.() -> OpenShiftAgentManager) =
        manager(OpenShiftAgentManager(resource, agentName)).start()

fun stopOpenShiftAgent(resource: OpenShiftResource, agentName: String, projectName: String) {
    OpenShiftAgentManager(resource, agentName).projectName(projectName).stop()
}

data class OpenShiftPort(val service: Int, val node: Int)

data class OpenShiftAgentPorts(val http: Int, val rsocket: Int, val custom: Map<String, OpenShiftPort>)

private val STARTING_LOCK = ReentrantLock()

class OpenShiftAgentManager(private val resource: OpenShiftResource, private val agentName: String) {
    private lateinit var projectName: String
    private var reusable: Boolean = false
    private var url: String = EMPTY_STRING
    private var image: String? = null
    private var pushTargetProject: String? = null
    private var pullSourceProject: String? = null
    private var platformProjectName: String = PLATFORM_CAMEL_CASE
    private var pullPolicy: String = IMAGE_PULL_POLICY_ALWAYS
    private var customPorts: MutableMap<String, String> = mutableMapOf()
    private var environmentVariables: MutableMap<String, String> = mutableMapOf()
    private var labels: MutableMap<String, String> = mutableMapOf()
    private var nodeSelector: MutableMap<String, String> = mutableMapOf()
    private val awaitingDeletion = AtomicBoolean(false)

    fun projectName(name: String): OpenShiftAgentManager {
        projectName = name
        return this
    }

    fun allowPushingTo(pushTargetProject: String): OpenShiftAgentManager {
        this.pushTargetProject = pushTargetProject
        return this
    }

    fun allowPullingFrom(pullSourceProject: String): OpenShiftAgentManager {
        this.pullSourceProject = pullSourceProject
        return this
    }

    fun platformProjectName(name: String?): OpenShiftAgentManager {
        platformProjectName = name ?: PLATFORM_CAMEL_CASE
        return this
    }

    fun image(image: String): OpenShiftAgentManager {
        this.image = image
        return this
    }

    fun reusable(reusable: Boolean = true): OpenShiftAgentManager {
        this.reusable = reusable
        return this
    }

    fun public(url: String): OpenShiftAgentManager {
        this.url = url
        return this
    }

    fun customPort(name: String, environmentVariableName: String): OpenShiftAgentManager {
        customPorts[name] = environmentVariableName
        return this
    }

    fun environmentVariable(name: String, value: String): OpenShiftAgentManager {
        environmentVariables[name] = value
        return this
    }

    fun label(name: String, value: String): OpenShiftAgentManager {
        labels[name] = value
        return this
    }

    fun nodeSelector(name: String, value: String): OpenShiftAgentManager {
        nodeSelector[name] = value
        return this
    }

    fun pullPolicy(policy: String): OpenShiftAgentManager {
        this.pullPolicy = policy
        return this
    }

    fun start(): OpenShiftAgentPorts = STARTING_LOCK.withLock {
        openShift(resource) {
            getProject(projectName) {
                getService(agentName)?.ports?.let { ports ->
                    if (!reusable) {
                        deleteRoute(agentName)
                        deleteService(agentName)
                        deletePod(agentName)
                        return@getProject createAgent(findAvailableTcpPort(AGENT_MIN_PORT), findAvailableTcpPort(AGENT_MAX_PORT))
                    }
                    if (awaitingDeletion.compareAndSet(true, false)) {
                        deleteRoute(agentName)
                        deleteService(agentName)
                        deletePod(agentName)
                        return@getProject createAgent(findAvailableTcpPort(AGENT_MIN_PORT), findAvailableTcpPort(AGENT_MAX_PORT))
                    }
                    getPod(agentName)?.let {
                        return@getProject OpenShiftAgentPorts(
                                http = ports.first { port -> port.name == HTTP_PORT_NAME }.nodePort.toInt(),
                                rsocket = ports.first { port -> port.name == RSOCKET_PORT_NAME }.nodePort.toInt(),
                                custom = ports.filter { port -> customPorts.containsKey(port.name) }
                                        .map { port -> port.name to OpenShiftPort(port.port, port.nodePort.toInt()) }
                                        .toMap())
                    }
                    return@getProject createAgent(
                            ports.first { port -> port.name == HTTP_PORT_NAME }.port,
                            ports.first { port -> port.name == RSOCKET_PORT_NAME }.port
                    )
                }

                getPod(agentName)?.let {
                    deletePod(agentName)
                    return@getProject createAgent(findAvailableTcpPort(AGENT_MIN_PORT), findAvailableTcpPort(AGENT_MAX_PORT))
                }

                return@getProject createAgent(findAvailableTcpPort(AGENT_MIN_PORT), findAvailableTcpPort(AGENT_MAX_PORT))
            } ?: createProject(projectName) {
                createAgent(findAvailableTcpPort(AGENT_MIN_PORT), findAvailableTcpPort(AGENT_MAX_PORT))
            }
        }
    }

    fun stop() = openShift(resource) {
        getProject(projectName)?.let { project ->
            if (awaitingDeletion.compareAndSet(false, true)) {
                deletePod(agentName, project.namespaceName)
                deleteService(agentName, project.namespaceName)
                deleteRoute(agentName, project.namespaceName)
            }
        }
    }

    private fun OpenShiftProjectService.createAgent(httpPort: Int, rsocketPort: Int): OpenShiftAgentPorts {
        allowImagesPulling(platformProjectName, projectName)
        allowImagesPushing(platformProjectName, projectName)
        pushTargetProject?.let { project -> allowImagesPushing(projectName, project) }
        pullSourceProject?.let { project -> allowImagesPulling(project, projectName) }

        val agentImage = image
                ?.let(::DockerImageURI)
                ?: throw PlatformException(PLATFORM_ERROR, "Agent image not presented")

        val hostName = "${extractHostName(url).replace(".${resource.applicationsDomain}", EMPTY_STRING)}.${resource.applicationsDomain}"
        createPod(agentName) {
            labels.forEach { (name, value) -> label(name, value) }
            nodeSelector.forEach { (name, value) -> nodeSelector(name, value) }
            container(agentName, agentImage) {
                tcpPort(rsocketPort, RSOCKET_PORT_NAME)
                environment(RSOCKET_PORT_PROPERTY, rsocketPort)
                tcpPort(httpPort, HTTP_PORT_NAME)
                environment(HTTP_PORT_PROPERTY, httpPort)
                environmentVariables.forEach { (name, value) -> environment(name, value) }
                customPorts.forEach { (name, environmentVariable) ->
                    val port = findAvailableTcpPort(AGENT_MIN_PORT, AGENT_MAX_PORT)
                    tcpPort(port, name)
                    environment(environmentVariable, port)
                }
                imagePullPolicy(pullPolicy)
            }
            serve(configuration.name) {
                if (url.isNotBlank()) {
                    public(name) {
                        host(hostName)
                        path(extractPath(url))
                        targetPort(httpPort)
                    }
                }
                asNodePort()
                return@serve this
            }
        }

        if (!waitForPodContainersReadiness(OpenShiftPodWaitingConfiguration(name = agentName))) {
            throw PlatformException(PLATFORM_ERROR, "Pod '$agentName' was not started correctly on OpenShift")
        }

        val ports = getService(agentName)
                ?.ports
                ?: throw PlatformException(PLATFORM_ERROR, "Service '$agentName' was not found for project")
        return OpenShiftAgentPorts(
                http = ports.first { port -> port.name == HTTP_PORT_NAME }.nodePort.toInt(),
                rsocket = ports.first { port -> port.name == RSOCKET_PORT_NAME }.nodePort.toInt(),
                custom = ports.filter { port -> customPorts.containsKey(port.name) }
                        .map { port -> port.name to OpenShiftPort(port.port, port.nodePort.toInt()) }
                        .toMap())
    }
}
