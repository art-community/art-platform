package ru.art.platform.open.shift.model

import com.openshift.restclient.images.DockerImageURI
import ru.art.platform.open.shift.constants.OpenShiftConstants.IMAGE_PULL_POLICY_ALWAYS
import ru.art.platform.open.shift.constants.OpenShiftConstants.POD_CHECKING_TIMEOUT
import ru.art.platform.open.shift.constants.OpenShiftConstants.POD_CHECK_WAITING_MILLIS
import ru.art.platform.open.shift.constants.OpenShiftConstants.POD_POLLING_DELAY_MILLIS
import ru.art.platform.open.shift.constants.OpenShiftConstants.TCP
import ru.art.platform.open.shift.model.OpenShiftServiceType.CLUSTER_IP


data class OpenShiftPodPort(val name: String,
                            var port: Int = 0,
                            var protocol: String = TCP)

data class OpenShiftProbes(val probePath: String? = null,
                           var livenessProbe: Boolean = false,
                           var readinessProbe: Boolean = false)


data class OpenShiftPortMapping(val name: String,
                                var servicePort: Int = 0,
                                var podPort: Int = 0,
                                var protocol: String = TCP)

data class OpenShiftPodConfiguration(val name: String,
                                     var initContainers: MutableMap<String, OpenShiftContainerConfiguration> = mutableMapOf(),
                                     var containers: MutableMap<String, OpenShiftContainerConfiguration> = mutableMapOf(),
                                     var nodeSelector: MutableMap<String, String> = mutableMapOf(),
                                     var volumes: MutableMap<String, OpenShiftVolumeConfiguration> = mutableMapOf(),
                                     var node: String? = null,
                                     var labels: MutableMap<String, String> = mutableMapOf(),
                                     var probes: OpenShiftProbes = OpenShiftProbes())

data class OpenShiftContainerConfiguration(val name: String,
                                           var image: DockerImageURI,
                                           var ports: MutableMap<String, OpenShiftPodPort> = mutableMapOf(),
                                           var volumeMounts: MutableMap<String, OpenShiftVolumeMountConfiguration> = mutableMapOf(),
                                           var imagePullPolicy: String = IMAGE_PULL_POLICY_ALWAYS,
                                           var arguments: MutableList<String> = mutableListOf(),
                                           var environment: MutableMap<String, String> = mutableMapOf(),
                                           var labels: MutableMap<String, String> = mutableMapOf())

data class OpenShiftDeploymentConfiguration(val name: String,
                                            var replicas: Int,
                                            var podConfiguration: OpenShiftPodConfiguration,
                                            var replicaSelector: MutableMap<String, String> = mutableMapOf(),
                                            var labels: MutableMap<String, String> = mutableMapOf())

enum class OpenShiftServiceType(val type: String) {
    CLUSTER_IP("ClusterIP"),
    LOAD_BALANCER("LoadBalancer"),
    EXTERNAL_NAME("ExternalName"),
    NODE_PORT("NodePort")
}

data class OpenShiftServiceConfiguration(val name: String,
                                         var type: OpenShiftServiceType = CLUSTER_IP,
                                         var clusterIp: String? = null,
                                         var portMapping: MutableMap<String, OpenShiftPortMapping> = mutableMapOf(),
                                         var nodePortMapping: MutableMap<String, Int> = mutableMapOf(),
                                         var podSelector: MutableMap<String, String> = mutableMapOf(),
                                         var labels: MutableMap<String, String> = mutableMapOf())

data class OpenShiftRouteConfiguration(val name: String,
                                       var service: String,
                                       var host: String? = null,
                                       var path: String? = null,
                                       var targetPort: Int? = null,
                                       var labels: MutableMap<String, String> = mutableMapOf())

data class OpenShiftVolumeMountConfiguration(val name: String, val path: String, val readOnly: Boolean)

data class OpenShiftVolumeConfiguration(val name: String, var configMap: String? = null, var secret: String? = null)

data class OpenShiftDeploymentPodCountWaitingConfiguration(val name: String,
                                                           val count: Int,
                                                           val includeOnlyReadPods: Boolean = false,
                                                           val timeoutMillis: Long = POD_CHECKING_TIMEOUT,
                                                           val pollingDelayMillis: Long = POD_POLLING_DELAY_MILLIS,
                                                           val beforeCheckWaitingMillis: Long = POD_CHECK_WAITING_MILLIS,
                                                           val afterCheckWaitingMillis: Long = POD_CHECK_WAITING_MILLIS)

data class OpenShiftDeploymentPodWaitingConfiguration(val name: String,
                                                      val minimalPodCount: Int,
                                                      val timeoutMillis: Long = POD_CHECKING_TIMEOUT,
                                                      val pollingDelayMillis: Long = POD_POLLING_DELAY_MILLIS,
                                                      val beforeCheckWaitingMillis: Long = POD_CHECK_WAITING_MILLIS,
                                                      val afterCheckWaitingMillis: Long = POD_CHECK_WAITING_MILLIS)

data class OpenShiftPodWaitingConfiguration(val name: String,
                                            val timeoutMillis: Long = POD_CHECKING_TIMEOUT,
                                            val pollingDelayMillis: Long = POD_POLLING_DELAY_MILLIS,
                                            val beforeCheckWaitingMillis: Long = POD_CHECK_WAITING_MILLIS,
                                            val afterCheckWaitingMillis: Long = POD_CHECK_WAITING_MILLIS)

data class OpenShiftDeletedPodWaitingConfiguration(val name: String,
                                                   val timeoutMillis: Long = POD_CHECKING_TIMEOUT,
                                                   val pollingDelayMillis: Long = POD_POLLING_DELAY_MILLIS)
