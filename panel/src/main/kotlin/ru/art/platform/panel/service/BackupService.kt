package ru.art.platform.panel.service

import ru.art.json.descriptor.JsonEntityWriter.writeJson
import ru.art.logging.LoggingModule.*
import ru.art.platform.api.mapping.configuration.PreparedConfigurationMapper.fromPreparedConfiguration
import ru.art.platform.api.mapping.module.ModuleMapper.fromModule
import ru.art.platform.api.mapping.project.ProjectMapper.fromProject
import ru.art.platform.git.service.GitService.add
import ru.art.platform.git.service.GitService.commit
import ru.art.platform.git.service.GitService.fetchRemoteReference
import ru.art.platform.git.service.GitService.push
import ru.art.platform.panel.repository.GitResourceRepository.getGitResource
import ru.art.platform.panel.repository.ModuleRepository.getProjectModules
import ru.art.platform.panel.repository.PreparedConfigurationsRepository.getPreparedConfigurations
import ru.art.platform.panel.repository.ProjectRepository.getProjects
import ru.art.platform.panel.service.ManagementService.getVersion
import ru.art.task.deferred.executor.SchedulerModuleActions.asynchronousPeriod
import ru.art.task.deferred.executor.TaskFactory.uniqueRunnableTask
import java.nio.file.Paths.get
import java.time.Duration.ofHours

object BackupService {
    fun scheduleBackup() {
        asynchronousPeriod(uniqueRunnableTask(::processBackup), ofHours(4))
    }

    fun processBackup() {
        getProjects().forEach { project ->
            try {
                val backupDirectory = "/tmp/${project.externalId.id}"
                val gitResource = getGitResource(project.gitResourceId.id)
                val path = fetchRemoteReference(gitResource, backupDirectory, "platform-backup/${getVersion()}")
                writeJson(fromProject.map(project), get("${path.toAbsolutePath()}/platform-backup/${getVersion()}/project.json"))
                getProjectModules(project.id).forEach { module ->
                    writeJson(fromModule.map(module), get("${path.toAbsolutePath()}/platform-backup/${getVersion()}/${module.externalId.id}.json"))
                }
                getPreparedConfigurations().forEach { configuration ->
                    val jsonPath = "${path.toAbsolutePath()}/platform-backup/${getVersion()}/${configuration.profile}-${configuration.name}.json"
                    writeJson(fromPreparedConfiguration.map(configuration), get(jsonPath))
                }
                add(backupDirectory)
                commit(backupDirectory, "Backup platform configuration for platform version: ${getVersion()}")
                push(gitResource, backupDirectory, "platform-backup/${getVersion()}")
            } catch (throwable: Throwable) {
                loggingModule().getLogger(BackupService::class.java).error(throwable.message, throwable)
            }
        }
    }
}
