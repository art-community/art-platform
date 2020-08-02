package ru.art.platform.open.shift.configurator

import ru.art.platform.open.shift.model.*
import ru.art.platform.open.shift.service.*

class OpenShiftDeploymentConfigurator(private val projectService: OpenShiftProjectService, val name: String) {
    val configuration: OpenShiftDeploymentConfiguration = OpenShiftDeploymentConfiguration(name, 0, OpenShiftPodConfiguration(name))

    fun replicas(replicas: Int): OpenShiftDeploymentConfigurator {
        configuration.replicas = replicas
        return this
    }

    fun pod(configurator: OpenShiftPodConfigurator.() -> OpenShiftPodConfigurator = { this }): OpenShiftDeploymentConfigurator {
        this.configuration.podConfiguration = configurator(OpenShiftPodConfigurator(projectService, name)).configuration
        replicaSelector(this.configuration.podConfiguration.labels)
        return this
    }

    fun pod(configuration: OpenShiftPodConfiguration): OpenShiftDeploymentConfigurator {
        this.configuration.podConfiguration = configuration
        replicaSelector(this.configuration.podConfiguration.labels)
        return this
    }

    fun label(name: String, value: String): OpenShiftDeploymentConfigurator {
        configuration.labels[name] = value
        return this
    }

    fun replicaSelector(name: String, value: String): OpenShiftDeploymentConfigurator {
        configuration.replicaSelector[name] = value
        return this
    }

    fun replicaSelector(selector: Map<String, String>): OpenShiftDeploymentConfigurator {
        configuration.replicaSelector = selector.toMutableMap()
        return this
    }
}