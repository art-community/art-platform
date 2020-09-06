package ru.art.platform.panel.configurator

import ru.art.config.extensions.ConfigExtensions.configString
import ru.art.platform.panel.constants.ConfigKeys.AGENT_IMAGE_KEY
import ru.art.platform.panel.constants.ConfigKeys.OPEN_SHIFT_PLATFORM_PROJECT_NAME_KEY
import ru.art.platform.open.shift.manager.OpenShiftAgentManager

object OpenShiftAgentConfigurator {
    fun OpenShiftAgentManager.configure() {
        image(configString(AGENT_IMAGE_KEY))
        platformProjectName(configString(OPEN_SHIFT_PLATFORM_PROJECT_NAME_KEY))
    }
}
