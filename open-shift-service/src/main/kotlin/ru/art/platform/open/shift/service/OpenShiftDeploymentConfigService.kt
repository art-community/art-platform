package ru.art.platform.open.shift.service

import com.openshift.internal.restclient.model.DeploymentConfig
import com.openshift.restclient.ResourceKind.DEPLOYMENT_CONFIG
import com.openshift.restclient.model.IDeploymentConfig
import com.openshift.restclient.model.IProject
import org.jboss.dmr.ModelNode.fromJSONString
import ru.art.core.constants.StringConstants.DASH
import ru.art.logging.LoggingModule.loggingModule
import ru.art.platform.open.shift.configurator.OpenShiftDeploymentConfigurator
import ru.art.platform.open.shift.constants.OpenShiftConstants.APPS_OPEN_SHIFT_VERSION_V_1
import ru.art.platform.open.shift.constants.OpenShiftConstants.DEPLOY_POD_POSTFIX
import ru.art.platform.open.shift.constants.OpenShiftConstants.SPEC
import ru.art.platform.open.shift.constants.OpenShiftConstants.TEMPLATE
import ru.art.platform.open.shift.constants.containerStatus
import ru.art.platform.open.shift.model.OpenShiftDeploymentPodCountWaitingConfiguration
import ru.art.platform.open.shift.model.OpenShiftDeploymentPodWaitingConfiguration
import java.lang.System.currentTimeMillis
import java.lang.Thread.sleep

fun OpenShiftService.createDeploymentConfig(name: String, project: IProject, configurator: OpenShiftDeploymentConfigurator.() -> OpenShiftDeploymentConfigurator = { this }): IDeploymentConfig {
    val deploymentConfig = buildDeploymentConfig(name, project, configurator)
    loggingModule().getLogger(OpenShiftService::class.java).info("Creating deployment config:\n${deploymentConfig.toJson()}")
    return client.create<IDeploymentConfig>(deploymentConfig)
}

fun OpenShiftProjectService.createDeploymentConfig(name: String, configurator: OpenShiftDeploymentConfigurator.() -> OpenShiftDeploymentConfigurator = { this }): IDeploymentConfig =
        createDeploymentConfig(name, project, configurator)


fun OpenShiftService.buildDeploymentConfig(name: String, project: IProject, configurator: OpenShiftDeploymentConfigurator.() -> OpenShiftDeploymentConfigurator = { this }): DeploymentConfig {
    val configuration = configurator(OpenShiftDeploymentConfigurator(OpenShiftProjectService(project, resource), name)).configuration
    with(client.resourceFactory.create<DeploymentConfig>(APPS_OPEN_SHIFT_VERSION_V_1, DEPLOYMENT_CONFIG, configuration.name)) {
        setNamespace(project.namespaceName)
        replicaSelector = configuration.replicaSelector
        replicas = configuration.replicas

        with(fromJSONString(this@with.toJson())) {
            get(SPEC, TEMPLATE, SPEC).set(fromJSONString(buildPod(project, configuration.podConfiguration).toJson())[SPEC])
            return DeploymentConfig(this, client, emptyMap())
        }
    }
}

fun OpenShiftProjectService.buildDeploymentConfig(name: String, configurator: OpenShiftDeploymentConfigurator.() -> OpenShiftDeploymentConfigurator = { this }): DeploymentConfig =
        buildDeploymentConfig(name, project, configurator)


fun OpenShiftProjectService.updateDeploymentConfig(name: String, configurator: OpenShiftDeploymentConfigurator.() -> OpenShiftDeploymentConfigurator = { this }) {
    updateDeploymentConfig(name, project, configurator)
}

fun OpenShiftService.updateDeploymentConfig(name: String, project: IProject, configurator: OpenShiftDeploymentConfigurator.() -> OpenShiftDeploymentConfigurator = { this }) {
    val configuration = configurator(OpenShiftDeploymentConfigurator(OpenShiftProjectService(project, resource), name)).configuration
    val deploymentConfig = getDeploymentConfig(name, project.namespaceName)?.let { currentDeploymentConfig ->
        with(currentDeploymentConfig) {
            replicaSelector = configuration.replicaSelector
            replicas = configuration.replicas

            with(fromJSONString(this@with.toJson())) {
                get(SPEC, TEMPLATE, SPEC).set(fromJSONString(currentDeploymentConfig.toJson()).get(SPEC, TEMPLATE, SPEC))
                if (configuration.podConfiguration.containers.isNotEmpty()) {
                    get(SPEC, TEMPLATE, SPEC).set(fromJSONString(buildPod(project, configuration.podConfiguration).toJson())[SPEC])
                }
                DeploymentConfig(this, client, emptyMap())
            }
        }
    } ?: buildDeploymentConfig(name, project, configurator)
    loggingModule().getLogger(OpenShiftService::class.java).info("Updating deploymentConfig config:\n${deploymentConfig.toJson()}")
    client.update<IDeploymentConfig>(deploymentConfig)
}


fun OpenShiftProjectService.setDeploymentConfig(name: String, updater: IDeploymentConfig.() -> Unit) {
    setDeploymentConfig(name, project, updater)
}

fun OpenShiftService.setDeploymentConfig(name: String, project: IProject, updater: IDeploymentConfig.() -> Unit) {
    getDeploymentConfig(name, project.namespaceName)?.let { deploymentConfig ->
        updater(deploymentConfig)
        loggingModule().getLogger(OpenShiftService::class.java).info("Setting deploymentConfig config:\n${deploymentConfig.toJson()}")
        client.update<IDeploymentConfig>(deploymentConfig)
    }
}


fun OpenShiftService.getDeploymentConfig(name: String, namespace: String) =
        client.list<IDeploymentConfig>(DEPLOYMENT_CONFIG, namespace).find { config -> config.name == name }

fun OpenShiftProjectService.getDeploymentConfig(name: String) =
        getDeploymentConfig(name, project.namespaceName)


fun OpenShiftService.getDeploymentPods(name: String, namespace: String) =
        getPods(namespace).filter { pod -> pod.name.substringBeforeLast(DASH).substringBeforeLast(DASH) == name && !pod.name.endsWith(DEPLOY_POD_POSTFIX) }

fun OpenShiftProjectService.getDeploymentPods(name: String) =
        getDeploymentPods(name, project.namespaceName)


fun OpenShiftService.getLatestDeploymentPods(name: String, namespace: String) = getPods(namespace)
        .filter { pod -> pod.name.startsWith(name) && !pod.name.endsWith(DEPLOY_POD_POSTFIX) }
        .filter { pod -> pod.name.substringBeforeLast(DASH).endsWith(getDeploymentConfig(name, namespace)!!.latestVersionNumber.toString()) }

fun OpenShiftProjectService.getLatestDeploymentPods(name: String) =
        getLatestDeploymentPods(name, project.namespaceName)


fun OpenShiftService.deleteDeploymentConfig(name: String, namespace: String) {
    getDeploymentConfig(name, namespace)
            ?.let { client.delete(DEPLOYMENT_CONFIG, namespace, name) }
            ?.let { loggingModule().getLogger(OpenShiftService::class.java).info("Deleting deployment config '$namespace.$name'") }
            ?: return
}

fun OpenShiftProjectService.deleteDeploymentConfig(name: String) =
        deleteDeploymentConfig(name, project.namespaceName)


fun OpenShiftService.deleteDeploymentPods(name: String, namespace: String): Boolean = getDeploymentPods(name, namespace)
        .filter { pod -> pod.isReady }
        .all { pod -> deletePod(pod.name, namespace) }

fun OpenShiftProjectService.deleteDeploymentPods(name: String) =
        deleteDeploymentPods(name, project.namespaceName)


fun OpenShiftProjectService.waitForLatestDeploymentPodsReadiness(configuration: OpenShiftDeploymentPodWaitingConfiguration) =
        waitForLatestDeploymentPodsReadiness(configuration, project.namespaceName)

fun OpenShiftService.waitForLatestDeploymentPodsReadiness(configuration: OpenShiftDeploymentPodWaitingConfiguration, namespace: String): Boolean {
    with(configuration) {
        sleep(beforeCheckWaitingMillis)
        val started = currentTimeMillis()
        while (true) {
            val latestPods = getLatestDeploymentPods(name, namespace)
            if (latestPods.any { pod -> pod.containerStatus().failed() }) {
                return false
            }
            if (latestPods.filter { pod -> pod.containerStatus().ready() }.size >= minimalPodCount) {
                sleep(afterCheckWaitingMillis)
                return true
            }
            sleep(pollingDelayMillis)
            if (currentTimeMillis() - started >= timeoutMillis) {
                return false
            }
        }
    }
}


fun OpenShiftProjectService.waitForDeploymentPodsCount(configuration: OpenShiftDeploymentPodCountWaitingConfiguration) =
        waitForDeploymentPodsCount(configuration, project.namespaceName)

fun OpenShiftService.waitForDeploymentPodsCount(configuration: OpenShiftDeploymentPodCountWaitingConfiguration, namespace: String): Boolean {
    with(configuration) {
        sleep(beforeCheckWaitingMillis)
        val started = currentTimeMillis()
        while (true) {
            val deploymentPods = getDeploymentPods(name, namespace)
            if (deploymentPods.any { pod -> pod.containerStatus().failed() }) {
                return false
            }
            if (!includeOnlyReadPods && deploymentPods.size == count) {
                sleep(afterCheckWaitingMillis)
                return true
            }
            if (deploymentPods.filter { pod -> pod.containerStatus().ready() }.size == count) {
                sleep(afterCheckWaitingMillis)
                return true
            }
            sleep(pollingDelayMillis)
            if (currentTimeMillis() - started >= timeoutMillis) {
                return false
            }
        }
    }
}
