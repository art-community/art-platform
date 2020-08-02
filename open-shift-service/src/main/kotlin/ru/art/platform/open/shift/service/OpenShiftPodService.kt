package ru.art.platform.open.shift.service

import com.openshift.internal.restclient.model.Container
import com.openshift.internal.restclient.model.Pod
import com.openshift.internal.restclient.model.Port
import com.openshift.internal.restclient.model.volume.VolumeMount
import com.openshift.restclient.ResourceKind.POD
import com.openshift.restclient.model.IPod
import com.openshift.restclient.model.IProject
import org.jboss.dmr.ModelNode
import org.jboss.dmr.ModelNode.fromJSONString
import ru.art.logging.LoggingModule.loggingModule
import ru.art.platform.open.shift.configurator.OpenShiftPodConfigurator
import ru.art.platform.open.shift.constants.OpenShiftConstants.CONFIG_MAP
import ru.art.platform.open.shift.constants.OpenShiftConstants.EMPTY_DIR
import ru.art.platform.open.shift.constants.OpenShiftConstants.IMAGE_PULL_POLICY_ALWAYS
import ru.art.platform.open.shift.constants.OpenShiftConstants.INIT_CONTAINERS
import ru.art.platform.open.shift.constants.OpenShiftConstants.NAME
import ru.art.platform.open.shift.constants.OpenShiftConstants.NODE_NAME
import ru.art.platform.open.shift.constants.OpenShiftConstants.NODE_SELECTOR
import ru.art.platform.open.shift.constants.OpenShiftConstants.SECRET
import ru.art.platform.open.shift.constants.OpenShiftConstants.SECRET_NAME
import ru.art.platform.open.shift.constants.OpenShiftConstants.SPEC
import ru.art.platform.open.shift.constants.OpenShiftConstants.VERSION_V_1
import ru.art.platform.open.shift.constants.OpenShiftConstants.VOLUMES
import ru.art.platform.open.shift.constants.containerStatus
import ru.art.platform.open.shift.model.OpenShiftDeletedPodWaitingConfiguration
import ru.art.platform.open.shift.model.OpenShiftPodConfiguration
import ru.art.platform.open.shift.model.OpenShiftPodWaitingConfiguration
import java.lang.System.currentTimeMillis
import java.lang.Thread.sleep

fun OpenShiftProjectService.createPod(podName: String, configurator: OpenShiftPodConfigurator.() -> OpenShiftPodConfigurator = { this }): IPod =
        createPod(podName, project, configurator)

fun OpenShiftService.createPod(podName: String, project: IProject, configurator: OpenShiftPodConfigurator.() -> OpenShiftPodConfigurator = { this }): IPod {
    val pod = buildPod(podName, project, configurator)
    loggingModule().getLogger(OpenShiftService::class.java).info("Creating pod:\n${pod.toJson()}")
    return client.create(pod)
}


fun OpenShiftProjectService.buildPod(podName: String, configurator: OpenShiftPodConfigurator.() -> OpenShiftPodConfigurator = { this }): IPod =
        buildPod(podName, project, configurator)

fun OpenShiftService.buildPod(podName: String, project: IProject, configurator: OpenShiftPodConfigurator.() -> OpenShiftPodConfigurator = { this }): IPod =
        buildPod(project, configurator(OpenShiftPodConfigurator(OpenShiftProjectService(project, resource), podName)).configuration)


fun OpenShiftProjectService.buildPod(configuration: OpenShiftPodConfiguration): IPod =
        buildPod(project, configuration)

fun OpenShiftService.buildPod(project: IProject, configuration: OpenShiftPodConfiguration): IPod {
    with(client.resourceFactory.create<Pod>(VERSION_V_1, POD, configuration.name)) {
        setNamespace(project.namespaceName)
        configuration.labels.forEach(::addLabel)
        configuration.containers.values.forEach { container ->
            with(addContainer(container.name)) {
                image = container.image
                imagePullPolicy = container.imagePullPolicy
                container.environment.forEach(::addEnvVar)
                ports = container.ports
                        .values
                        .map { port ->
                            Port(ModelNode()).apply {
                                name = port.name
                                containerPort = port.port
                                protocol = port.protocol
                            }
                        }.toSet()
                volumeMounts = container
                        .volumeMounts
                        .map { mount ->
                            VolumeMount(ModelNode()).apply {
                                name = mount.key
                                mountPath = mount.value.path
                                isReadOnly = mount.value.readOnly
                            }
                        }
                        .toSet()
                commandArgs = container.arguments
            }
        }
        with(fromJSONString(toJson())) {
            if (!configuration.node.isNullOrBlank()) {
                get(SPEC, NODE_NAME).set(configuration.node)
            }
            configuration.nodeSelector.forEach { (label, value) ->
                get(SPEC, NODE_SELECTOR).set(label, value)
            }
            configuration.volumes.forEach { (volumeName, volumeConfiguration) ->
                get(SPEC, VOLUMES).add().apply {
                    get(NAME).set(volumeName)
                    if (!volumeConfiguration.configMap.isNullOrBlank()) {
                        get(CONFIG_MAP, NAME).set(volumeConfiguration.configMap)
                        return@apply
                    }
                    if (!volumeConfiguration.secret.isNullOrBlank()) {
                        get(SECRET, SECRET_NAME).set(volumeConfiguration.secret)
                        return@apply
                    }
                    get(EMPTY_DIR).setEmptyObject()
                }
            }
            configuration.initContainers.values.forEach { initContainer ->
                with(Container(get(SPEC, INIT_CONTAINERS).add())) {
                    name = initContainer.name
                    image = initContainer.image
                    imagePullPolicy = IMAGE_PULL_POLICY_ALWAYS
                    initContainer.environment.forEach(::addEnvVar)
                    ports = initContainer.ports
                            .values
                            .map { port ->
                                Port(ModelNode()).apply {
                                    name = port.name
                                    containerPort = port.port
                                    protocol = port.protocol
                                }
                            }.toSet()
                    volumeMounts = initContainer.volumeMounts
                            .map { mount ->
                                VolumeMount(ModelNode()).apply {
                                    name = mount.key
                                    mountPath = mount.value.path
                                    isReadOnly = mount.value.readOnly
                                }
                            }
                            .toSet()
                    commandArgs = initContainer.arguments
                }
            }
            return Pod(this, client, emptyMap())
        }
    }
}


fun OpenShiftService.getPod(name: String, namespace: String) =
        client.list<Pod>(POD, namespace).find { foundPod -> foundPod.name == name }

fun OpenShiftProjectService.getPod(name: String) =
        getPod(name, project.namespaceName)


fun OpenShiftService.getPods(namespace: String): List<Pod> =
        client.list(POD, namespace)

fun OpenShiftProjectService.getPods(): List<Pod> =
        getPods(project.namespaceName)


fun OpenShiftService.deletePod(name: String, namespace: String): Boolean {
    getPod(name, namespace)
            ?.let { client.delete(POD, namespace, name) }
            ?.let { loggingModule().getLogger(OpenShiftService::class.java).info("Deleting pod '$namespace.$name'") }
            ?: return true
    return waitForPodDeleted(OpenShiftDeletedPodWaitingConfiguration(name = name), namespace)
}

fun OpenShiftProjectService.deletePod(name: String) {
    deletePod(name, project.namespaceName)
}


fun OpenShiftProjectService.waitForPodContainersReadiness(configuration: OpenShiftPodWaitingConfiguration) =
        waitForPodContainersReadiness(configuration, project.namespaceName)

fun OpenShiftService.waitForPodContainersReadiness(configuration: OpenShiftPodWaitingConfiguration, namespace: String): Boolean {
    with(configuration) {
        sleep(beforeCheckWaitingMillis)
        val started = currentTimeMillis()
        while (true) {
            val status = getPod(name, namespace)?.containerStatus()
            if (status?.failed() == true) {
                return false
            }
            if (status?.ready() == true) {
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


fun OpenShiftProjectService.waitForPodDeleted(configuration: OpenShiftDeletedPodWaitingConfiguration) =
        waitForPodDeleted(configuration, project.namespaceName)

fun OpenShiftService.waitForPodDeleted(configuration: OpenShiftDeletedPodWaitingConfiguration, namespace: String): Boolean {
    with(configuration) {
        val started = currentTimeMillis()
        while (getPod(name, namespace) != null) {
            sleep(pollingDelayMillis)
            if (currentTimeMillis() - started >= timeoutMillis) {
                return false
            }
        }
    }
    return true
}
