package ru.art.platform.agent.dsl

import com.openshift.restclient.images.DockerImageURI
import com.openshift.restclient.model.IDeploymentConfig
import org.apache.commons.lang.RandomStringUtils.randomAlphanumeric
import ru.art.core.constants.StringConstants.*
import ru.art.platform.agent.constants.OpenShiftConstants.DEPLOYMENT_TRIGGER_VARIABLE_NAME
import ru.art.platform.agent.constants.OpenShiftConstants.TRIGGER_LENGTH
import ru.art.platform.agent.openShift.compareOpenShiftApplications
import ru.art.platform.agent.openShift.configureOpenShiftApplications
import ru.art.platform.api.model.external.PortMapping
import ru.art.platform.api.model.module.ModuleApplications
import ru.art.platform.api.model.module.ProbesConfiguration
import ru.art.platform.api.model.resource.OpenShiftResource
import ru.art.platform.common.constants.Applications.APPLICATION_TYPES
import ru.art.platform.common.constants.ErrorCodes.UPDATING_FAILED
import ru.art.platform.common.constants.PlatformKeywords.CONFIGS_CAMEL_CASE
import ru.art.platform.common.constants.PlatformKeywords.FILES_CAMEL_CASE
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.common.extractor.extractHostName
import ru.art.platform.common.extractor.extractPath
import ru.art.platform.open.shift.configurator.OpenShiftDeploymentConfigurator
import ru.art.platform.open.shift.constants.OpenShiftConstants.TCP
import ru.art.platform.open.shift.model.OpenShiftDeploymentPodWaitingConfiguration
import ru.art.platform.open.shift.model.OpenShiftProbes
import ru.art.platform.open.shift.service.*
import java.util.Objects.isNull
import java.util.Objects.nonNull

class OpenShiftUpdater(private val resource: OpenShiftResource, private var name: String, private val image: String) {
    private lateinit var projectName: String
    private lateinit var workingDirectory: String
    private var routePath: String? = null
    private var routeTargetPort: Int? = null
    private var containerArgumentString: String? = null
    private var replicationsCount = 0
    private var internalIp: String? = null
    private var ports: MutableSet<PortMapping> = mutableSetOf()
    private var files: MutableMap<String, ByteArray> = mutableMapOf()
    private var configs: MutableMap<String, String> = mutableMapOf()
    private var environmentVariables = mutableMapOf<String, String>()
    private var skipChangesCheck = false
    private var applications: ModuleApplications? = null
    private var probes: OpenShiftProbes = OpenShiftProbes()

    fun skipChangesCheck(skip: Boolean = true): OpenShiftUpdater {
        skipChangesCheck = skip
        return this
    }

    fun publish(url: String): OpenShiftUpdater {
        routePath = url
        return this
    }

    fun internalIp(internalIp: String): OpenShiftUpdater {
        this.internalIp = internalIp
        return this
    }

    fun publish(url: String, targetPort: Int): OpenShiftUpdater {
        routePath = url
        routeTargetPort = targetPort
        return this
    }

    fun replicationsCount(count: Int): OpenShiftUpdater {
        replicationsCount = count
        return this
    }

    fun port(port: PortMapping): OpenShiftUpdater {
        ports.add(port)
        return this
    }

    fun addPorts(ports: Set<PortMapping>): OpenShiftUpdater {
        this.ports.addAll(ports)
        return this
    }

    fun ports(ports: Set<PortMapping>): OpenShiftUpdater {
        this.ports = ports.toMutableSet()
        return this
    }

    fun stringFile(name: String, content: String): OpenShiftUpdater {
        this.files[name] = content.toByteArray()
        return this
    }

    fun stringFiles(files: Map<String, String>): OpenShiftUpdater {
        this.files = files.mapValues { string -> string.value.toByteArray() }.toMutableMap()
        return this
    }

    fun addStringFiles(files: Map<String, String>): OpenShiftUpdater {
        this.files.putAll(files.mapValues { string -> string.value.toByteArray() })
        return this
    }

    fun binaryFile(name: String, content: ByteArray): OpenShiftUpdater {
        this.files[name] = content
        return this
    }

    fun binaryFiles(files: Map<String, ByteArray>): OpenShiftUpdater {
        this.files = files.toMutableMap()
        return this
    }

    fun addBinaryFiles(files: Map<String, ByteArray>): OpenShiftUpdater {
        this.files.putAll(files.toMutableMap())
        return this
    }

    fun config(fileName: String, content: String): OpenShiftUpdater {
        configs[fileName] = content
        return this
    }

    fun configs(configs: Map<String, String>): OpenShiftUpdater {
        this.configs.putAll(configs)
        return this
    }

    fun containerArguments(arguments: String): OpenShiftUpdater {
        containerArgumentString = arguments
        return this
    }

    fun environmentVariable(name: String, value: String): OpenShiftUpdater {
        environmentVariables[name] = value
        return this
    }

    fun projectName(projectName: String): OpenShiftUpdater {
        this.projectName = projectName
        return this
    }

    fun workingDirectory(workingDirectory: String): OpenShiftUpdater {
        this.workingDirectory = workingDirectory
        return this
    }

    fun applications(applications: ModuleApplications): OpenShiftUpdater {
        this.applications = applications
        return this
    }

    fun probes(probeConfiguration: ProbesConfiguration?): OpenShiftUpdater {
        if (isNull(probeConfiguration)) return this

        this.probes = OpenShiftProbes(
                probePath = probeConfiguration?.path,
                livenessProbe = probeConfiguration!!.isLivenessProbe,
                readinessProbe = probeConfiguration.isReadinessProbe
        )
        return this
    }

    fun update(then: OpenShiftUpdateResult.() -> Unit) {
        openShift(resource) {
            getProject(projectName) {
                updateDeployment()
                if (!waitForLatestDeploymentPodsReadiness(OpenShiftDeploymentPodWaitingConfiguration(name = name, minimalPodCount = replicationsCount))) {
                    throw PlatformException(UPDATING_FAILED, "Ready pods count less than '$replicationsCount' after timeout duration")
                }
                val routeUrl = routePath
                        ?.substringBefore(SCHEME_DELIMITER)
                        ?.let { protocol ->
                            val hostName = extractHostName(routePath!!).replace("$DOT${resource.applicationsDomain}", EMPTY_STRING)
                            "$protocol$SCHEME_DELIMITER$hostName$DOT${resource.applicationsDomain}${extractPath(routePath!!)}"
                        }
                getService(name)?.let { service ->
                    then(OpenShiftUpdateResult(
                            clusterIp = service.clusterIP,
                            routeUrl = routeUrl,
                            nodePortMapping = service.ports.map { port -> port.port to port.nodePort.toInt() }.toMap())
                    )
                    return@getProject this
                }
                then(OpenShiftUpdateResult(routeUrl = routeUrl, nodePortMapping = emptyMap()))
            }
        }
    }


    private fun OpenShiftProjectService.updateDeployment() {
        getDeploymentConfig(name)?.let { current ->
            if (skipChangesCheck) {
                return@let this
            }

            val configsEquals = configsEquals()
            val filesEquals = filesEquals()
            val environmentEquals = environmentEquals(current)
            val podEquals = podEquals(current)
            val applicationsEquals = compareOpenShiftApplications(applications, current)
                    .byConfigMaps(getConfigMaps().filter { configMap -> configMap.name.startsWith(name) })
                    .applicationsEquals()

            if (environmentEquals && configsEquals && filesEquals && podEquals && applicationsEquals) {
                if (current.replicas != replicationsCount) {
                    updateDeploymentConfig(name) { replicas(replicationsCount) }
                }
                updateService()
                updateRoute()
                return
            }

            return@let this
        }?.let {
            updateDeploymentConfig(name) {
                environmentVariable(DEPLOYMENT_TRIGGER_VARIABLE_NAME, randomAlphanumeric(TRIGGER_LENGTH))
                configurePod(this)
            }
        } ?: createDeploymentConfig(name) {
            environmentVariable(DEPLOYMENT_TRIGGER_VARIABLE_NAME, randomAlphanumeric(TRIGGER_LENGTH))
            configurePod(this)
        }
    }

    private fun OpenShiftProjectService.configurePod(configurator: OpenShiftDeploymentConfigurator) = with(configurator) {
        replicas(replicationsCount)
        pod {
            if (configs.isEmpty()) {
                deleteConfigMap("$name-$CONFIGS_CAMEL_CASE")
            } else {
                configMap(CONFIGS_CAMEL_CASE, "$name-$CONFIGS_CAMEL_CASE", configs)
            }

            if (files.isEmpty()) {
                deleteSecret("$name-$FILES_CAMEL_CASE")
            } else {
                secret(FILES_CAMEL_CASE, "$name-$FILES_CAMEL_CASE", files)
            }

            val configMaps = getConfigMaps()
            APPLICATION_TYPES.forEach { type ->
                configMaps
                        .filter { configMap -> configMap.name.startsWith("$name-$type") }
                        .forEach { configMap -> deleteConfigMap(configMap.name) }
            }

            applications?.let { applications ->
                environmentVariables.putAll(configureOpenShiftApplications(workingDirectory).addApplications(applications))
            }

            probe(probes)

            container(name, DockerImageURI(image)) {
                alwaysPullImage()
                ports.forEach { port -> tcpPort(port.internalPort) }
                containerArgumentString?.split(SPACE)?.forEach { argument -> argument(argument) }
                environmentVariables.forEach { (key, value) -> environment(key, value) }
                if (configs.isNotEmpty()) {
                    volumeMount(CONFIGS_CAMEL_CASE, "$workingDirectory/$CONFIGS_CAMEL_CASE", true)
                }
                if (files.isNotEmpty()) {
                    volumeMount(FILES_CAMEL_CASE, "$workingDirectory/$FILES_CAMEL_CASE", true)
                }
                return@container this
            }

            if (ports.isEmpty()) {
                deleteService(name)
                deleteRoute(name)
                return@pod this
            }

            updateService()
            updateRoute()
            return@pod this
        }
    }

    private fun OpenShiftProjectService.updateRoute() {
        if (routePath.isNullOrBlank()) {
            deleteRoute(name)
            return
        }
        if (isNull(routeTargetPort)) {
            routeTargetPort = ports.first().internalPort
        }
        val hostName = "${extractHostName(routePath!!).replace("$DOT${resource.applicationsDomain}", EMPTY_STRING)}$DOT${resource.applicationsDomain}"
        val path = extractPath(routePath!!)

        val routeEquals = getRoute(name)?.let { route ->
            route.host == hostName &&
                    route.path == path &&
                    route.port.targetPort == routeTargetPort
        } ?: false

        if (routeEquals) {
            return
        }

        deleteRoute(name)
        createRoute(name, name) {
            host(hostName)
            path(path)
            targetPort(routeTargetPort!!)
        }
        return
    }

    private fun OpenShiftProjectService.updateService() {
        val clusterIp = internalIp
        val ports = ports

        if (ports.isEmpty()) {
            deleteService(name)
            return
        }

        val serviceEquals = getService(name)?.let { service ->
            service.clusterIP == clusterIp &&
                    ports.map { port -> port.internalPort to port.externalPort.toInt() }.toMap() == service.ports.map { port -> port.port to port.nodePort.toInt() }.toMap()
        } ?: false

        if (serviceEquals) {
            return
        }

        deleteService(name)
        createService(name) {
            select(name)
            clusterIp?.let(::ip)
            ports.forEach { port -> tcpPort(port.internalPort, "${TCP.toLowerCase()}-${port.internalPort}") }
            asNodePort(ports
                    .filter { port -> nonNull(port.externalPort) }
                    .map { port -> "${TCP.toLowerCase()}-${port.internalPort}" to port.externalPort }
                    .toMap())
        }
        return
    }

    private fun OpenShiftProjectService.podEquals(current: IDeploymentConfig): Boolean = getDeploymentPods(name).let { pods ->
        pods.isNotEmpty() && pods.all { pod ->
            val moduleContainerIndex = pod.containers.indexOfFirst { container -> container.name == name }
            val moduleImage = pod.images.elementAt(moduleContainerIndex)
            val imageEquals = image == moduleImage
            val moduleContainer = pod.containers.elementAt(moduleContainerIndex)

            val argumentsEquals = containerArgumentString
                    ?.let { arguments -> arguments == moduleContainer.commandArgs?.joinToString(SPACE) }
                    ?: moduleContainer.commandArgs?.joinToString(SPACE).isNullOrEmpty()

            val portsEquals = (ports.isEmpty() && moduleContainer.ports.isEmpty()) || ports.any { port ->
                moduleContainer.ports.any { containerPot -> containerPot.containerPort == port.internalPort }
            }

            val applicationsEquals = compareOpenShiftApplications(applications, current).byPods(pod).applicationsEquals()

            imageEquals && argumentsEquals && portsEquals && applicationsEquals
        }
    }

    private fun OpenShiftProjectService.filesEquals(): Boolean = getSecret("$name-$FILES_CAMEL_CASE")
            ?.let { secret ->
                files.isNotEmpty()
                        && secret.size() == files.size
                        && files.all { file -> secret.extractSecretData(file.key).contentEquals(file.value) }
            }
            ?: files.isEmpty()

    private fun OpenShiftProjectService.configsEquals(): Boolean = getConfigMap("$name-$CONFIGS_CAMEL_CASE")
            ?.let { configMap -> configMap.data == configs }
            ?: configs.isEmpty()

    private fun environmentEquals(current: IDeploymentConfig): Boolean = current
            .environmentVariables
            ?.filter { variable -> !variable.name.startsWith(DEPLOYMENT_TRIGGER_VARIABLE_NAME) }
            ?.map { variable -> variable.name to variable.value }
            ?.toMap() == environmentVariables
}

data class OpenShiftUpdateResult(val clusterIp: String? = null, val routeUrl: String? = null, val nodePortMapping: Map<Int, Int> = emptyMap())
