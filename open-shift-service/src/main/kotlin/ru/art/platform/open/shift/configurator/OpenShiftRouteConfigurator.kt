package ru.art.platform.open.shift.configurator

import ru.art.platform.open.shift.model.*

class OpenShiftRouteConfigurator(val name: String, private val serviceName: String, val configuration: OpenShiftRouteConfiguration = OpenShiftRouteConfiguration(name, serviceName)) {
    fun service(name: String): OpenShiftRouteConfigurator {
        configuration.service = name
        return this
    }

    fun host(host: String): OpenShiftRouteConfigurator {
        configuration.host = host
        return this
    }

    fun path(path: String): OpenShiftRouteConfigurator {
        configuration.path = path
        return this
    }

    fun label(name: String, value: String): OpenShiftRouteConfigurator {
        configuration.labels[name] = value
        return this
    }

    fun targetPort(targetPort: Int): OpenShiftRouteConfigurator {
        configuration.targetPort = targetPort
        return this
    }
}