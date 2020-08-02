package ru.art.platform.agent.dsl

import com.openshift.restclient.images.DockerImageURI
import org.apache.commons.lang.RandomStringUtils.randomAlphanumeric
import ru.art.core.constants.StringConstants.*
import ru.art.platform.agent.constants.OpenShiftConstants.DEPLOYMENT_TRIGGER_VARIABLE_NAME
import ru.art.platform.agent.constants.OpenShiftConstants.TRIGGER_LENGTH
import ru.art.platform.agent.openShift.configureOpenShiftApplications
import ru.art.platform.api.model.module.ModuleApplications
import ru.art.platform.api.model.resource.OpenShiftResource
import ru.art.platform.common.constants.Applications.APPLICATION_TYPES
import ru.art.platform.common.constants.ErrorCodes.INSTALLATION_FAILED
import ru.art.platform.common.constants.PlatformKeywords.CONFIGS_CAMEL_CASE
import ru.art.platform.common.constants.PlatformKeywords.FILES_CAMEL_CASE
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.common.extractor.extractHostName
import ru.art.platform.common.extractor.extractPath
import ru.art.platform.open.shift.configurator.OpenShiftDeploymentConfigurator
import ru.art.platform.open.shift.model.OpenShiftDeploymentPodWaitingConfiguration
import ru.art.platform.open.shift.service.*
import java.util.Objects.isNull

class OpenShiftInstaller(private val resource: OpenShiftResource, private var name: String, private val image: String) {
    private lateinit var projectName: String
    private lateinit var workingDirectory: String
    private var routePath: String? = null
    private var routeTargetPort: Int? = null
    private var containerArgumentsString: String? = null
    private var replicationsCount = 0
    private var ports: MutableSet<Int> = mutableSetOf()
    private var files: MutableMap<String, ByteArray> = mutableMapOf()
    private var configs: MutableMap<String, String> = mutableMapOf()
    private var environmentVariables = mutableMapOf<String, String>()
    private var applications: ModuleApplications? = null

    fun publish(url: String): OpenShiftInstaller {
        routePath = url
        return this
    }

    fun publish(url: String, targetPort: Int): OpenShiftInstaller {
        routePath = url
        routeTargetPort = targetPort
        return this
    }

    fun replicationsCount(count: Int): OpenShiftInstaller {
        replicationsCount = count
        return this
    }

    fun port(port: Int): OpenShiftInstaller {
        ports.add(port)
        return this
    }

    fun addPorts(ports: Set<Int>): OpenShiftInstaller {
        this.ports.addAll(ports)
        return this
    }

    fun ports(ports: Set<Int>): OpenShiftInstaller {
        this.ports = ports.toMutableSet()
        return this
    }

    fun stringFile(name: String, content: String): OpenShiftInstaller {
        this.files[name] = content.toByteArray()
        return this
    }

    fun stringFiles(files: Map<String, String>): OpenShiftInstaller {
        this.files = files.mapValues { string -> string.value.toByteArray() }.toMutableMap()
        return this
    }

    fun addStringFiles(files: Map<String, String>): OpenShiftInstaller {
        this.files.putAll(files.mapValues { string -> string.value.toByteArray() })
        return this
    }

    fun binaryFile(name: String, content: ByteArray): OpenShiftInstaller {
        this.files[name] = content
        return this
    }

    fun binaryFiles(files: Map<String, ByteArray>): OpenShiftInstaller {
        this.files = files.toMutableMap()
        return this
    }

    fun addBinaryFiles(files: Map<String, ByteArray>): OpenShiftInstaller {
        this.files.putAll(files.toMutableMap())
        return this
    }

    fun config(fileName: String, content: String): OpenShiftInstaller {
        configs[fileName] = content
        return this
    }

    fun configs(configs: Map<String, String>): OpenShiftInstaller {
        this.configs.putAll(configs)
        return this
    }

    fun containerArguments(arguments: String): OpenShiftInstaller {
        containerArgumentsString = arguments
        return this
    }

    fun environmentVariable(name: String, value: String): OpenShiftInstaller {
        environmentVariables[name] = value
        return this
    }

    fun projectName(projectName: String): OpenShiftInstaller {
        this.projectName = projectName
        return this
    }

    fun workingDirectory(workingDirectory: String): OpenShiftInstaller {
        this.workingDirectory = workingDirectory
        return this
    }

    fun applications(applications: ModuleApplications): OpenShiftInstaller {
        this.applications = applications
        return this
    }

    fun install(then: OpenShiftInstallationResult.() -> Unit = {}): OpenShiftInstaller {
        environmentVariable(DEPLOYMENT_TRIGGER_VARIABLE_NAME, randomAlphanumeric(TRIGGER_LENGTH))
        openShift(resource) {
            getProject(projectName) {
                deleteApplicationConfigs()
                getDeploymentConfig(name)?.let { deleteDeploymentConfig(name) }
                createDeploymentConfig(name) { configurePod() }

                if (!waitForLatestDeploymentPodsReadiness(OpenShiftDeploymentPodWaitingConfiguration(name = name, minimalPodCount = 1))) {
                    throw PlatformException(INSTALLATION_FAILED, "No one pod has been ready after timeout duration")
                }

                getService(name)?.let { service ->
                    val routeUrl = routePath
                            ?.substringBefore(SCHEME_DELIMITER)
                            ?.let { protocol ->
                                val hostName = extractHostName(routePath!!).replace("$DOT${resource.applicationsDomain}", EMPTY_STRING)
                                "$protocol$SCHEME_DELIMITER$hostName$DOT${resource.applicationsDomain}${extractPath(routePath!!)}"
                            }
                    then(OpenShiftInstallationResult(
                            clusterIp = service.clusterIP,
                            routeUrl = routeUrl,
                            nodePortMapping = service.ports.map { port -> port.port to port.nodePort.toInt() }.toMap())
                    )
                    return@getProject this
                }
                then(OpenShiftInstallationResult(nodePortMapping = emptyMap()))
            }
        }
        return this
    }

    private fun OpenShiftDeploymentConfigurator.configurePod(): OpenShiftDeploymentConfigurator {
        replicas(replicationsCount)
        pod {
            if (configs.isNotEmpty()) {
                configMap(CONFIGS_CAMEL_CASE, "$name-$CONFIGS_CAMEL_CASE", configs)
            }

            if (files.isNotEmpty()) {
                secret(FILES_CAMEL_CASE, "$name-$FILES_CAMEL_CASE", files)
            }

            applications?.let(configureOpenShiftApplications(workingDirectory)::addApplications)?.let(environmentVariables::putAll)

            container(name, DockerImageURI(image)) {
                alwaysPullImage()
                ports.forEach { port -> tcpPort(port) }
                containerArgumentsString?.split(SPACE)?.forEach { argument -> argument(argument) }
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
                return@pod this
            }

            serve(name) {
                asNodePort()
                if (routePath.isNullOrBlank()) {
                    return@serve this
                }
                if (isNull(routeTargetPort)) {
                    routeTargetPort = ports.first()
                }
                val hostName = "${extractHostName(routePath!!).replace("$DOT${resource.applicationsDomain}", EMPTY_STRING)}$DOT${resource.applicationsDomain}"
                public(name) {
                    host(hostName)
                    path(extractPath(routePath!!))
                    targetPort(routeTargetPort!!)
                }
                return@serve this
            }
        }
        return this
    }

    private fun OpenShiftProjectService.deleteApplicationConfigs() {
        val configMaps = getConfigMaps()
        APPLICATION_TYPES.forEach { application ->
            configMaps
                    .filter { configMap -> configMap.name.startsWith("$name-$application") }
                    .forEach { configMap -> deleteConfigMap(configMap.name) }
        }
    }
}

data class OpenShiftInstallationResult(val clusterIp: String? = null, val routeUrl: String? = null, val nodePortMapping: Map<Int, Int> = emptyMap())
