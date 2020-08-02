package ru.art.platform.agent.dsl

import org.apache.commons.lang.RandomStringUtils.randomAlphanumeric
import ru.art.platform.agent.constants.OpenShiftConstants.DEPLOYMENT_TRIGGER_VARIABLE_NAME
import ru.art.platform.agent.constants.OpenShiftConstants.TRIGGER_LENGTH
import ru.art.platform.api.model.resource.OpenShiftResource
import ru.art.platform.common.constants.ErrorCodes.RESTARTING_FAILED
import ru.art.platform.common.exception.PlatformException
import ru.art.platform.open.shift.constants.OpenShiftConstants.IMAGE_PULL_POLICY_IF_NOT_PRESENT
import ru.art.platform.open.shift.model.OpenShiftDeploymentPodWaitingConfiguration
import ru.art.platform.open.shift.service.getProject
import ru.art.platform.open.shift.service.openShift
import ru.art.platform.open.shift.service.setDeploymentConfig
import ru.art.platform.open.shift.service.waitForLatestDeploymentPodsReadiness

fun restartDeploymentOnOpenShift(resource: OpenShiftResource, projectName: String, name: String, newCount: Int) {
    openShift(resource) {
        getProject(projectName) {
            setDeploymentConfig(name) {
                getContainer(name).imagePullPolicy = IMAGE_PULL_POLICY_IF_NOT_PRESENT
                replicas = newCount
                setEnvironmentVariable(DEPLOYMENT_TRIGGER_VARIABLE_NAME, randomAlphanumeric(TRIGGER_LENGTH))
            }
            if (!waitForLatestDeploymentPodsReadiness(OpenShiftDeploymentPodWaitingConfiguration(name = name, minimalPodCount = newCount))) {
                throw PlatformException(RESTARTING_FAILED, "Deployment pods restarting failed")
            }
        }
    }
}