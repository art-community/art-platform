package ru.art.platform.agent.openShift

import com.openshift.internal.restclient.model.Pod
import com.openshift.restclient.images.DockerImageURI
import com.openshift.restclient.model.IConfigMap
import com.openshift.restclient.model.IDeploymentConfig
import ru.art.platform.agent.constants.OpenShiftConstants.DEPLOYMENT_TRIGGER_VARIABLE_NAME
import ru.art.platform.api.model.module.ModuleApplications
import ru.art.platform.common.constants.Applications.*
import ru.art.platform.common.constants.PlatformKeywords.APPLICATIONS_CAMEL_CASE

private fun hasAnyApplication(current: IDeploymentConfig) = current
        .environmentVariables
        .any { variable -> variable.name.startsWith("$DEPLOYMENT_TRIGGER_VARIABLE_NAME-$APPLICATIONS_CAMEL_CASE") }

private fun hasApplicationOfType(type: String, current: IDeploymentConfig) = current
        .environmentVariables
        .any { variable -> variable.name.startsWith("$DEPLOYMENT_TRIGGER_VARIABLE_NAME-$APPLICATIONS_CAMEL_CASE-$type") }

class OpenShiftModuleApplicationsPodComparator(private val applications: ModuleApplications?,
                                               private val deploymentConfig: IDeploymentConfig,
                                               private val pod: Pod) {
    fun applicationsEquals() = applications?.let { applications ->
        APPLICATION_TYPES.all { type ->
            when (type) {
                FILEBEAT -> filebeatEquals(applications)
                else -> true
            }
        }
    } ?: !hasAnyApplication(deploymentConfig)

    private fun filebeatEquals(applications: ModuleApplications): Boolean = applications.filebeat
            ?.let { filebeat ->
                pod.containers.any { container ->
                    container.name == FILEBEAT && container.image == DockerImageURI(filebeat.url) && container.ports.any { port -> filebeat.port == port.containerPort }
                }
            }
            ?: !hasApplicationOfType(FILEBEAT, deploymentConfig)
}


class OpenShiftModuleApplicationsConfigMapComparator(private val applications: ModuleApplications?,
                                                     private val deploymentConfig: IDeploymentConfig,
                                                     private val configMaps: List<IConfigMap>) {
    fun applicationsEquals() = applications?.let { applications ->
        APPLICATION_TYPES.all { type ->
            when (type) {
                FILEBEAT -> {
                    val filebeatConfigMap = configMaps.firstOrNull { configMap -> configMap.name.startsWith("${deploymentConfig.name}-$FILEBEAT") }
                    filebeatEquals(applications, filebeatConfigMap)
                }
                else -> true
            }
        }
    } ?: !hasAnyApplication(deploymentConfig)


    private fun filebeatEquals(applications: ModuleApplications, configMap: IConfigMap?): Boolean {
        configMap ?: return applications.filebeat?.configuration.isNullOrBlank()
        return applications.filebeat
                ?.let { filebeat -> configMap.data.any { files -> files.key == FILEBEAT_CONFIG_FILE && files.value == filebeat.configuration } }
                ?: configMap.data.none { files -> files.key == FILEBEAT_CONFIG_FILE }
    }
}

class OpenShiftModuleApplicationsComparator(private val applications: ModuleApplications?, private val deploymentConfig: IDeploymentConfig) {
    fun byPods(pod: Pod) = OpenShiftModuleApplicationsPodComparator(applications, deploymentConfig, pod)

    fun byConfigMaps(configMaps: List<IConfigMap>) = OpenShiftModuleApplicationsConfigMapComparator(applications, deploymentConfig, configMaps)
}

fun compareOpenShiftApplications(applications: ModuleApplications?, deploymentConfig: IDeploymentConfig) =
        OpenShiftModuleApplicationsComparator(applications, deploymentConfig)
