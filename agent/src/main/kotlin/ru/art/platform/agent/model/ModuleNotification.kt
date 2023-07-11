package ru.art.platform.agent.model

data class ModuleNotification(
        val projectName: String,
        val moduleName: String,
        val moduleVersion: String,
        val user: String?,
        val additionalMessage: String?
)
