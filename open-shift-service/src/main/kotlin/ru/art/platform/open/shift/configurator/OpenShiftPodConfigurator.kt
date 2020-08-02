package ru.art.platform.open.shift.configurator

import com.openshift.restclient.images.*
import ru.art.platform.open.shift.constants.OpenShiftConstants.POD_GROUP
import ru.art.platform.open.shift.constants.OpenShiftConstants.SERVICE_PREFIX
import ru.art.platform.open.shift.constants.OpenShiftConstants.TCP
import ru.art.platform.open.shift.constants.OpenShiftConstants.UDP
import ru.art.platform.open.shift.model.*
import ru.art.platform.open.shift.service.*

class OpenShiftPodConfigurator(private val projectService: OpenShiftProjectService, val name: String) {
    val configuration: OpenShiftPodConfiguration = OpenShiftPodConfiguration(name)

    init {
        configuration.labels[POD_GROUP] = configuration.name
    }

    fun container(name: String, image: DockerImageURI, configurator: OpenShiftContainerConfigurator.() -> OpenShiftContainerConfigurator): OpenShiftPodConfigurator {
        configuration.containers[name] = configurator(OpenShiftContainerConfigurator(name, image)).configuration
        return this
    }

    fun node(node: String): OpenShiftPodConfigurator {
        this.configuration.node = node
        return this
    }

    fun initContainer(name: String, image: DockerImageURI, configurator: OpenShiftContainerConfigurator.() -> OpenShiftContainerConfigurator): OpenShiftPodConfigurator {
        configuration.initContainers[name] = configurator(OpenShiftContainerConfigurator(name, image)).configuration
        return this
    }

    fun label(name: String, value: String): OpenShiftPodConfigurator {
        configuration.labels[name] = value
        return this
    }

    fun nodeSelector(name: String, value: String): OpenShiftPodConfigurator {
        configuration.nodeSelector[name] = value
        return this
    }

    fun group(value: String): OpenShiftPodConfigurator {
        configuration.labels[POD_GROUP] = value
        return this
    }

    fun volume(name: String): OpenShiftPodConfigurator {
        configuration.volumes[name] = OpenShiftVolumeConfiguration(name)
        return this
    }

    fun configMap(volumeName: String, configMapName: String, content: Map<String, String>): OpenShiftPodConfigurator {
        projectService.deleteConfigMap(configMapName)
        projectService.createConfigMap(configMapName, content)
        configuration.volumes[volumeName] = OpenShiftVolumeConfiguration(volumeName, configMap = configMapName)
        return this
    }

    fun putConfigs(configMapName: String, content: Map<String, String>): OpenShiftPodConfigurator {
        projectService.putConfigMapContent(configMapName, content)
        return this
    }

    fun secret(volumeName: String, secretName: String, content: Map<String, ByteArray>): OpenShiftPodConfigurator {
        projectService.deleteSecret(secretName)
        projectService.createSecret(secretName, content)
        configuration.volumes[volumeName] = OpenShiftVolumeConfiguration(volumeName, secret = secretName)
        return this
    }


    fun configMap(volumeName: String, configMapName: String): OpenShiftPodConfigurator {
        configuration.volumes[volumeName] = OpenShiftVolumeConfiguration(volumeName, configMap = configMapName)
        return this
    }

    fun secret(volumeName: String, secretName: String): OpenShiftPodConfigurator {
        configuration.volumes[volumeName] = OpenShiftVolumeConfiguration(volumeName, secret = secretName)
        return this
    }


    fun serve(service: String = "$SERVICE_PREFIX-${name}", configurator: OpenShiftServiceConfigurator.() -> OpenShiftServiceConfigurator = { this }): OpenShiftPodConfigurator {
        projectService.deleteService(service)
        projectService.createService(service) {
            this@OpenShiftPodConfigurator
                    .configuration
                    .containers
                    .values
                    .forEach { container ->
                        container.ports
                                .values
                                .forEach { port ->
                                    when (port.protocol) {
                                        TCP -> tcpPort(port.port, port.name)
                                        UDP -> udpPort(port.port, port.name)
                                    }
                                }
                    }
            this@OpenShiftPodConfigurator
                    .configuration
                    .initContainers
                    .values
                    .forEach { container ->
                        container.ports
                                .values
                                .forEach { port ->
                                    when (port.protocol) {
                                        TCP -> tcpPort(port.port, port.name)
                                        UDP -> udpPort(port.port, port.name)
                                    }
                                }
                    }
            select(this@OpenShiftPodConfigurator)
            configurator(this)
        }
        return this
    }
}
