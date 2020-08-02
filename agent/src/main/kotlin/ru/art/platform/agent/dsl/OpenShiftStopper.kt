package ru.art.platform.agent.dsl

import ru.art.platform.api.model.resource.OpenShiftResource
import ru.art.platform.common.constants.ErrorCodes.STOPPING_FAILED
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.open.shift.model.OpenShiftDeploymentPodCountWaitingConfiguration
import ru.art.platform.open.shift.service.getProject
import ru.art.platform.open.shift.service.openShift
import ru.art.platform.open.shift.service.setDeploymentConfig
import ru.art.platform.open.shift.service.waitForDeploymentPodsCount

fun stopDeploymentOnOpenShift(resource: OpenShiftResource, projectName: String, name: String) {
    openShift(resource) {
        getProject(projectName) {
            setDeploymentConfig(name) { replicas = 0 }
            if (!waitForDeploymentPodsCount(OpenShiftDeploymentPodCountWaitingConfiguration(name = name, count = 0))) {
                throw PlatformException(STOPPING_FAILED, "Deployment pods termination failed")
            }
        }
    }
}