package ru.art.platform.agent.service


import ru.art.platform.agent.builder.NotificationMessageBuilder.buildDeleteModuleErrorMessage
import ru.art.platform.agent.builder.NotificationMessageBuilder.buildDeleteModuleSuccessMessage
import ru.art.platform.agent.builder.NotificationMessageBuilder.buildInstallModuleErrorMessage
import ru.art.platform.agent.builder.NotificationMessageBuilder.buildInstallModuleModuleSuccessMessage
import ru.art.platform.agent.builder.NotificationMessageBuilder.buildRestartModuleErrorMessage
import ru.art.platform.agent.builder.NotificationMessageBuilder.buildRestartModuleSuccessMessage
import ru.art.platform.agent.builder.NotificationMessageBuilder.buildStopModuleErrorMessage
import ru.art.platform.agent.builder.NotificationMessageBuilder.buildStopModuleSuccessMessage
import ru.art.platform.agent.builder.NotificationMessageBuilder.buildUpdateModuleErrorMessage
import ru.art.platform.agent.builder.NotificationMessageBuilder.buildUpdateModuleSuccessMessage
import ru.art.platform.agent.extension.findProxyResource
import ru.art.platform.agent.model.ModuleNotification
import ru.art.platform.agent.service.IntegramService.sendTelegramMessage
import ru.art.platform.api.model.project.ProjectNotificationsConfiguration
import ru.art.platform.api.model.request.*
import ru.art.platform.api.model.resource.ProxyResource
import ru.art.task.deferred.executor.SchedulerModuleActions.asynchronous

object NotificationManagerService {
    fun sendInstallSuccessNotification(request: AgentModuleInstallRequest) {
        if (request.notificationsConfiguration?.url.isNullOrEmpty()) return
        with (request) {
            val notification = ModuleNotification(
                    projectName = projectId.id,
                    moduleName = module.name,
                    moduleVersion = module.artifact.version,
                    user = user.fullName,
                    additionalMessage = notificationsConfiguration.additionalMessage.orEmpty()
            )
            sendNotification(buildInstallModuleModuleSuccessMessage(notification), notificationsConfiguration, proxyResources)
        }
    }

    fun sendInstallErrorNotification(request: AgentModuleInstallRequest) {
        if (request.notificationsConfiguration?.url.isNullOrEmpty()) return
        with (request) {
            val notification = ModuleNotification(
                    projectName = projectId.id,
                    moduleName = module.name,
                    moduleVersion = module.artifact.version,
                    user = user.fullName,
                    additionalMessage = notificationsConfiguration.additionalMessage.orEmpty()
            )
            sendNotification(buildInstallModuleErrorMessage(notification), notificationsConfiguration, proxyResources)
        }
    }

    fun sendUpdateSuccessNotification(request: AgentModuleUpdateRequest) {
        if (request.notificationsConfiguration?.url.isNullOrEmpty()) return
        with (request) {
            val notification = ModuleNotification(
                    projectName = projectId.id,
                    moduleName = newModule.name,
                    moduleVersion = newModule.artifact.version,
                    user = user.fullName,
                    additionalMessage = notificationsConfiguration.additionalMessage.orEmpty()
            )
            sendNotification(buildUpdateModuleSuccessMessage(notification), notificationsConfiguration, proxyResources)
        }
    }

    fun sendUpdateErrorNotification(request: AgentModuleUpdateRequest) {
        if (request.notificationsConfiguration?.url.isNullOrEmpty()) return
        with (request) {
            val notification = ModuleNotification(
                    projectName = projectId.id,
                    moduleName = newModule.name,
                    moduleVersion = newModule.artifact.version,
                    user = user.fullName,
                    additionalMessage = notificationsConfiguration.additionalMessage.orEmpty()
            )
            sendNotification(buildUpdateModuleErrorMessage(notification), notificationsConfiguration, proxyResources)
        }
    }

    fun sendStopSuccessNotification(request: AgentModuleStopRequest) {
        if (request.notificationsConfiguration?.url.isNullOrEmpty()) return
        with (request) {
            val notification = ModuleNotification(
                    projectName = projectId.id,
                    moduleName = module.name,
                    moduleVersion = module.artifact.version,
                    user = user.fullName,
                    additionalMessage = notificationsConfiguration.additionalMessage.orEmpty()
            )
            sendNotification(buildStopModuleSuccessMessage(notification), notificationsConfiguration, proxyResources)
        }
    }

    fun sendStopErrorNotification(request: AgentModuleStopRequest) {
        if (request.notificationsConfiguration?.url.isNullOrEmpty()) return
        with (request) {
            val notification = ModuleNotification(
                    projectName = projectId.id,
                    moduleName = module.name,
                    moduleVersion = module.artifact.version,
                    user = user.fullName,
                    additionalMessage = notificationsConfiguration.additionalMessage.orEmpty()
            )
            sendNotification(buildStopModuleErrorMessage(notification), notificationsConfiguration, proxyResources)
        }
    }

    fun sendRestartSuccessNotification(request: AgentModuleRestartRequest) {
        if (request.notificationsConfiguration?.url.isNullOrEmpty()) return
        with (request) {
            val notification = ModuleNotification(
                    projectName = projectId.id,
                    moduleName = module.name,
                    moduleVersion = module.artifact.version,
                    user = user.fullName,
                    additionalMessage = notificationsConfiguration.additionalMessage.orEmpty()
            )
            sendNotification(buildRestartModuleSuccessMessage(notification), notificationsConfiguration, proxyResources)
        }
    }

    fun sendRestartErrorNotification(request: AgentModuleRestartRequest) {
        if (request.notificationsConfiguration?.url.isNullOrEmpty()) return
        with (request) {
            val notification = ModuleNotification(
                    projectName = projectId.id,
                    moduleName = module.name,
                    moduleVersion = module.artifact.version,
                    user = user.fullName,
                    additionalMessage = notificationsConfiguration.additionalMessage.orEmpty()
            )
            sendNotification(buildRestartModuleErrorMessage(notification), notificationsConfiguration, proxyResources)
        }
    }

    fun sendDeleteSuccessNotification(request: AgentModuleDeleteRequest) {
        if (request.notificationsConfiguration?.url.isNullOrEmpty()) return
        with (request) {
            val notification = ModuleNotification(
                    projectName = projectId.id,
                    moduleName = module.name,
                    moduleVersion = module.artifact.version,
                    user = user.fullName,
                    additionalMessage = notificationsConfiguration.additionalMessage.orEmpty()
            )
            sendNotification(buildDeleteModuleSuccessMessage(notification), notificationsConfiguration, proxyResources)
        }
    }

    fun sendDeleteErrorNotification(request: AgentModuleDeleteRequest) {
        if (request.notificationsConfiguration?.url.isNullOrEmpty()) return
        with (request) {
            val notification = ModuleNotification(
                    projectName = projectId.id,
                    moduleName = module.name,
                    moduleVersion = module.artifact.version,
                    user = user.fullName,
                    additionalMessage = notificationsConfiguration.additionalMessage.orEmpty()
            )
            sendNotification(buildDeleteModuleErrorMessage(notification), notificationsConfiguration, proxyResources)
        }
    }

    private fun sendNotification(message: String, configuration: ProjectNotificationsConfiguration, proxies: Set<ProxyResource>) {
        asynchronous {
            configuration
                    .proxyId
                    ?.let { proxy -> sendTelegramMessage(message, configuration.url, proxies.findProxyResource(proxy)) }
                    ?: sendTelegramMessage(message, configuration.url)
        }
    }
}
