package ru.art.platform.agent.dsl

import ru.art.platform.api.model.resource.OpenShiftResource
import ru.art.platform.common.constants.Applications.FILEBEAT
import ru.art.platform.common.constants.ErrorCodes.DELETING_FAILED
import ru.art.platform.common.constants.PlatformKeywords.CONFIGS_CAMEL_CASE
import ru.art.platform.common.constants.PlatformKeywords.FILES_CAMEL_CASE
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.open.shift.service.*

fun deleteDeploymentOnOpenShift(resource: OpenShiftResource, projectName: String, name: String) {
    openShift(resource) {
        getProject(projectName) {
            deleteDeploymentConfig(name)
            deleteService(name)
            deleteRoute(name)
            deleteSecret("$name-$FILES_CAMEL_CASE")
            deleteConfigMap("$name-$CONFIGS_CAMEL_CASE")
            deleteConfigMap("$name-$FILEBEAT")
            if (!deleteDeploymentPods(name)) {
                throw PlatformException(DELETING_FAILED, "Deployment pods termination failed")
            }
        }
    }
}