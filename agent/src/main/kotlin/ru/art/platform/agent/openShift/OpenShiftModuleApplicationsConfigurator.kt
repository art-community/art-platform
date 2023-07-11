package ru.art.platform.agent.openShift

import com.openshift.restclient.images.DockerImageURI
import org.apache.commons.lang.RandomStringUtils.randomAlphanumeric
import ru.art.platform.agent.constants.ApplicationConstants.FILEBEAT_CONFIG_PATH
import ru.art.platform.agent.constants.ApplicationConstants.FILEBEAT_DATA_PATH
import ru.art.platform.agent.constants.OpenShiftConstants.DEPLOYMENT_TRIGGER_VARIABLE_NAME
import ru.art.platform.agent.constants.OpenShiftConstants.TRIGGER_LENGTH
import ru.art.platform.api.model.module.ModuleApplications
import ru.art.platform.common.constants.Applications.*
import ru.art.platform.common.constants.PlatformKeywords
import ru.art.platform.common.constants.PlatformKeywords.APPLICATIONS_CAMEL_CASE
import ru.art.platform.open.shift.configurator.OpenShiftPodConfigurator

class OpenShiftModuleApplicationsConfigurator(private val workingDirectory: String, private val podConfigurator: OpenShiftPodConfigurator) {
    fun addApplications(applications: ModuleApplications): Map<String, String> {
        val applicationTriggers = mutableMapOf<String, String>()
        applications.filebeat?.let { filebeat ->
            podConfigurator.apply {
                container(FILEBEAT, DockerImageURI(filebeat.url)) {
                    alwaysPullImage()
                    tcpPort(filebeat.port)

                    volumeMount(FILEBEAT_DATA, workingDirectory + FILEBEAT_DATA_PATH)
                    volume(FILEBEAT_DATA)

                    configMap(FILEBEAT_CONFIG, "${podConfigurator.name}-$FILEBEAT", mapOf(FILEBEAT_CONFIG_FILE to filebeat.configuration))
                    volumeMount(FILEBEAT_CONFIG, FILEBEAT_CONFIG_PATH)
                }
            }
            applicationTriggers["$DEPLOYMENT_TRIGGER_VARIABLE_NAME-$APPLICATIONS_CAMEL_CASE-$FILEBEAT"] = randomAlphanumeric(TRIGGER_LENGTH)
        }
        return applicationTriggers
    }
}

fun OpenShiftPodConfigurator.configureOpenShiftApplications(workingDirectory: String) =
        OpenShiftModuleApplicationsConfigurator(workingDirectory, this)
