package ru.art.platform.open.shift.configurator

import com.openshift.restclient.images.*
import ru.art.platform.open.shift.constants.*
import ru.art.platform.open.shift.constants.OpenShiftConstants.IMAGE_PULL_POLICY_ALWAYS
import ru.art.platform.open.shift.constants.OpenShiftConstants.IMAGE_PULL_POLICY_IF_NOT_PRESENT
import ru.art.platform.open.shift.constants.OpenShiftConstants.IMAGE_PULL_POLICY_NEVER
import ru.art.platform.open.shift.constants.OpenShiftConstants.TCP
import ru.art.platform.open.shift.model.*

class OpenShiftContainerConfigurator(val name: String, val image: DockerImageURI) {
    val configuration: OpenShiftContainerConfiguration = OpenShiftContainerConfiguration(name, image)

    fun tcpPort(port: Int, name: String = "${TCP.toLowerCase()}-$port"): OpenShiftContainerConfigurator {
        configuration.ports[name] = OpenShiftPodPort(name, port, TCP)
        return this
    }

    fun udpPort(port: Int, name: String = "${TCP.toLowerCase()}-$port"): OpenShiftContainerConfigurator {
        configuration.ports[name] = OpenShiftPodPort(name, port, OpenShiftConstants.UDP)
        return this
    }

    fun imagePullPolicy(imagePullPolicy: String): OpenShiftContainerConfigurator {
        this.configuration.imagePullPolicy = imagePullPolicy
        return this
    }

    fun alwaysPullImage(): OpenShiftContainerConfigurator {
        this.configuration.imagePullPolicy = IMAGE_PULL_POLICY_ALWAYS
        return this
    }

    fun neverPullImage(): OpenShiftContainerConfigurator {
        this.configuration.imagePullPolicy = IMAGE_PULL_POLICY_NEVER
        return this
    }

    fun pullImageIfNotPresent(): OpenShiftContainerConfigurator {
        this.configuration.imagePullPolicy = IMAGE_PULL_POLICY_IF_NOT_PRESENT
        return this
    }

    fun port(port: Int, protocol: String, name: String = "${protocol.toLowerCase()}-$port"): OpenShiftContainerConfigurator {
        configuration.ports[name] = OpenShiftPodPort(name, port, protocol)
        return this
    }

    fun volumeMount(volume: String, path: String, readOnly: Boolean = false): OpenShiftContainerConfigurator {
        configuration.volumeMounts[volume] = OpenShiftVolumeMountConfiguration(volume, path, readOnly)
        return this
    }

    fun argument(argument: String): OpenShiftContainerConfigurator {
        configuration.arguments.add(argument)
        return this
    }

    fun environment(name: String, value: Any): OpenShiftContainerConfigurator {
        configuration.environment[name] = value.toString()
        return this
    }
}