package ru.art.platform.agent.builder

import ru.art.platform.agent.constants.NotificationManagerConstants.NOTIFICATION_MANAGER_FAILED_ICON
import ru.art.platform.agent.constants.NotificationManagerConstants.NOTIFICATION_MANAGER_STOP_ICON
import ru.art.platform.agent.constants.NotificationManagerConstants.NOTIFICATION_MANAGER_SUCCESS_ICON
import ru.art.platform.agent.model.ModuleNotification

object NotificationMessageBuilder {
    fun buildInstallModuleModuleSuccessMessage(moduleNotification: ModuleNotification): String = "$NOTIFICATION_MANAGER_SUCCESS_ICON Установка модуля ${moduleNotification.moduleName}:${moduleNotification.moduleVersion} " +
            "проекта ${moduleNotification.projectName} завершена: ${moduleNotification.user}. ${moduleNotification.additionalMessage}"

    fun buildInstallModuleErrorMessage(moduleNotification: ModuleNotification): String = "$NOTIFICATION_MANAGER_FAILED_ICON Ошибка при установке модуля ${moduleNotification.moduleName}:${moduleNotification.moduleVersion} " +
            "проекта ${moduleNotification.projectName}: ${moduleNotification.user}. ${moduleNotification.additionalMessage}"

    fun buildUpdateModuleSuccessMessage(moduleNotification: ModuleNotification): String = "$NOTIFICATION_MANAGER_SUCCESS_ICON Обновление модуля ${moduleNotification.moduleName}:${moduleNotification.moduleVersion} " +
            "проекта ${moduleNotification.projectName} завершено: ${moduleNotification.user}. ${moduleNotification.additionalMessage}"

    fun buildUpdateModuleErrorMessage(moduleNotification: ModuleNotification): String = "$NOTIFICATION_MANAGER_FAILED_ICON Ошибка при обновлении модуля ${moduleNotification.moduleName}:${moduleNotification.moduleVersion} " +
            "проекта ${moduleNotification.projectName}: ${moduleNotification.user}. ${moduleNotification.additionalMessage}"

    fun buildStopModuleSuccessMessage(moduleNotification: ModuleNotification): String = "$NOTIFICATION_MANAGER_STOP_ICON Модуль ${moduleNotification.moduleName}:${moduleNotification.moduleVersion} " +
            "проекта ${moduleNotification.projectName} остановлен: ${moduleNotification.user}. ${moduleNotification.additionalMessage}"

    fun buildStopModuleErrorMessage(moduleNotification: ModuleNotification): String = "$NOTIFICATION_MANAGER_FAILED_ICON Во время остановки модуля ${moduleNotification.moduleName}:${moduleNotification.moduleVersion} " +
            "проекта ${moduleNotification.projectName} произошла ошибка: ${moduleNotification.user}. ${moduleNotification.additionalMessage}"

    fun buildRestartModuleSuccessMessage(moduleNotification: ModuleNotification): String = "$NOTIFICATION_MANAGER_SUCCESS_ICON Рестарт модуля ${moduleNotification.moduleName}:${moduleNotification.moduleVersion} " +
            "проекта ${moduleNotification.projectName} завершен: ${moduleNotification.user}. ${moduleNotification.additionalMessage}"

    fun buildRestartModuleErrorMessage(moduleNotification: ModuleNotification): String = "$NOTIFICATION_MANAGER_FAILED_ICON Ошибка при рестарте модуля ${moduleNotification.moduleName}:${moduleNotification.moduleVersion} " +
            "проекта ${moduleNotification.projectName}: ${moduleNotification.user}. ${moduleNotification.additionalMessage}"

    fun buildDeleteModuleSuccessMessage(moduleNotification: ModuleNotification): String = "$NOTIFICATION_MANAGER_STOP_ICON Модуль ${moduleNotification.moduleName}:${moduleNotification.moduleVersion} " +
            "проекта ${moduleNotification.projectName} удален: ${moduleNotification.user}. ${moduleNotification.additionalMessage}"

    fun buildDeleteModuleErrorMessage(moduleNotification: ModuleNotification): String = "$NOTIFICATION_MANAGER_FAILED_ICON Ошибка при удалении модуля ${moduleNotification.moduleName}:${moduleNotification.moduleVersion} " +
            "проекта ${moduleNotification.projectName}: ${moduleNotification.user}. ${moduleNotification.additionalMessage}"
}
