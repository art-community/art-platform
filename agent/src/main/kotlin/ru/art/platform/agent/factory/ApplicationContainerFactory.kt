package ru.art.platform.agent.factory

import com.openshift.restclient.images.DockerImageURI
import ru.art.core.constants.StringConstants
import ru.art.core.constants.StringConstants.NEW_LINE
import ru.art.entity.Entity
import ru.art.platform.agent.constants.GrafanaConstants.IMAGE_URL
import ru.art.platform.agent.constants.GrafanaConstants.MESSAGE
import ru.art.platform.agent.constants.GrafanaConstants.STATE
import ru.art.platform.agent.constants.GrafanaConstants.TAGS
import ru.art.platform.common.constants.PlatformKeywords
import ru.art.platform.open.shift.configurator.OpenShiftPodConfigurator

object ApplicationContainerFactory {
//    fun basicGrafanaNotification(configurator: OpenShiftPodConfigurator): String {
//        configurator.configuration {
//            container(name, DockerImageURI(image)) {
//                alwaysPullImage()
//                ports.forEach { port -> tcpPort(port) }
//                containerArgumentsString?.split(StringConstants.SPACE)?.forEach { argument -> argument(argument) }
//                environmentVariables.forEach { (key, value) -> environment(key, value) }
//                if (configs.isNotEmpty()) {
//                    volumeMount(PlatformKeywords.CONFIGS_CAMEL_CASE, "$workingDirectory/${PlatformKeywords.CONFIGS_CAMEL_CASE}", true)
//                }
//                if (files.isNotEmpty()) {
//                    volumeMount(PlatformKeywords.FILES_CAMEL_CASE, "$workingDirectory/${PlatformKeywords.FILES_CAMEL_CASE}", true)
//                }
//                return@container this
//            }
//        }
//    }
}