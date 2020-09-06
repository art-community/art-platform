package ru.art.platform.panel.configurator

import ru.art.platform.api.mapping.filebeat.FilebeatModuleApplicationMapper.toFilebeatModuleApplication
import ru.art.platform.api.model.application.ModuleApplication
import ru.art.platform.api.model.filebeat.FilebeatModuleApplication
import ru.art.platform.api.model.module.ModuleApplications
import ru.art.platform.common.constants.Applications.FILEBEAT_APPLICATION
import ru.art.platform.panel.repository.FilebeatApplicationRepository.getFilebeatApplication

object ModuleApplicationConfigurator {
    fun configureModuleApplications(applications: List<ModuleApplication>): ModuleApplications {
        val builder = ModuleApplications.builder()

        applications.forEach { application ->
            when (FILEBEAT_APPLICATION) {
                application.applicationId.type -> builder.filebeat(filebeat(application))
            }
        }

        return builder.build()
    }

    private fun filebeat(moduleApplication: ModuleApplication): FilebeatModuleApplication {
        val filebeat = toFilebeatModuleApplication.map(moduleApplication.application)
        val application = getFilebeatApplication(moduleApplication.applicationId.id)
        return FilebeatModuleApplication.builder()
                .applicationId(moduleApplication.applicationId)
                .port(filebeat.port)
                .configuration(filebeat.configuration)
                .url(application.url)
                .resourceId(application.resourceId)
                .build()

    }
}
