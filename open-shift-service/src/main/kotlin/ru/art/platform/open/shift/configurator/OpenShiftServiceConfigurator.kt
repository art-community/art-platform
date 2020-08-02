package ru.art.platform.open.shift.configurator

import ru.art.platform.open.shift.constants.OpenShiftConstants.POD_GROUP
import ru.art.platform.open.shift.constants.OpenShiftConstants.ROUTE_PREFIX
import ru.art.platform.open.shift.constants.OpenShiftConstants.TCP
import ru.art.platform.open.shift.constants.OpenShiftConstants.TO
import ru.art.platform.open.shift.constants.OpenShiftConstants.UDP
import ru.art.platform.open.shift.model.OpenShiftPortMapping
import ru.art.platform.open.shift.model.OpenShiftServiceConfiguration
import ru.art.platform.open.shift.model.OpenShiftServiceType.NODE_PORT
import ru.art.platform.open.shift.service.OpenShiftProjectService
import ru.art.platform.open.shift.service.createRoute
import ru.art.platform.open.shift.service.deleteRoute

class OpenShiftServiceConfigurator(private val projectService: OpenShiftProjectService,
                                   val name: String,
                                   val configuration: OpenShiftServiceConfiguration = OpenShiftServiceConfiguration(name)) {
    fun tcpPort(port: Int, name: String = "$port-$TO-$port"): OpenShiftServiceConfigurator {
        configuration.portMapping[name] = OpenShiftPortMapping(name, port, port, TCP)
        return this
    }

    fun tcpPort(servicePort: Int, podPort: Int = servicePort, name: String = "$servicePort-$TO-$podPort"): OpenShiftServiceConfigurator {
        configuration.portMapping[name] = OpenShiftPortMapping(name, servicePort, podPort, TCP)
        return this
    }

    fun udpPort(port: Int, name: String = "$port-$TO-$port"): OpenShiftServiceConfigurator {
        configuration.portMapping[name] = OpenShiftPortMapping(name, port, port, UDP)
        return this
    }

    fun udpPort(servicePort: Int, podPort: Int = servicePort, name: String = "$servicePort-$TO-$podPort"): OpenShiftServiceConfigurator {
        configuration.portMapping[name] = OpenShiftPortMapping(name, servicePort, podPort, UDP)
        return this
    }

    fun port(servicePort: Int, podPort: Int = servicePort, protocol: String, name: String = "$servicePort-$TO-$podPort"): OpenShiftServiceConfigurator {
        configuration.portMapping[name] = OpenShiftPortMapping(name, servicePort, podPort, protocol)
        return this
    }

    fun label(name: String, value: String): OpenShiftServiceConfigurator {
        configuration.labels[name] = value
        return this
    }

    fun select(labelName: String, labelValue: String): OpenShiftServiceConfigurator {
        configuration.podSelector[labelName] = labelValue
        return this
    }

    fun select(podGroup: String): OpenShiftServiceConfigurator {
        configuration.podSelector[POD_GROUP] = podGroup
        return this
    }

    fun select(pod: OpenShiftPodConfigurator): OpenShiftServiceConfigurator {
        configuration.podSelector[POD_GROUP] = pod.configuration.labels[POD_GROUP] ?: pod.configuration.name
        return this
    }

    fun ip(ip: String): OpenShiftServiceConfigurator {
        configuration.clusterIp = ip
        return this
    }

    fun asNodePort(nodePortMapping: Map<String, Int> = emptyMap()): OpenShiftServiceConfigurator {
        configuration.type = NODE_PORT
        configuration.nodePortMapping = nodePortMapping.toMutableMap()
        return this
    }

    fun public(name: String = "$ROUTE_PREFIX-${this.name}", configurator: OpenShiftRouteConfigurator.() -> OpenShiftRouteConfigurator = {
        OpenShiftRouteConfigurator(name, this@OpenShiftServiceConfigurator.name)
    }): OpenShiftServiceConfigurator {
        projectService.deleteRoute(name)
        projectService.createRoute(name, this@OpenShiftServiceConfigurator.name, configurator)
        return this
    }
}
