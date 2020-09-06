package ru.art.platform.panel.service

import ru.art.platform.api.mapping.assembly.AssembledArtifactMapper
import ru.art.platform.api.mapping.external.ExternalIdentifierMapper
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper
import ru.art.platform.api.model.assembly.AssemblyConfiguration
import ru.art.platform.api.model.external.PortMapping
import ru.art.platform.api.model.file.StringFile
import ru.art.platform.api.model.module.Module
import ru.art.platform.api.model.module.ModuleUrl
import ru.art.platform.api.model.user.User
import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.panel.constants.MigrationStatus.PROCESSED
import ru.art.platform.panel.constants.Migrations.APPLICATIONS_MIGRATION
import ru.art.platform.panel.constants.Migrations.ASSEMBLY_CONFIGURATION_MIGRATION
import ru.art.platform.panel.constants.Migrations.MODULES_MIGRATION
import ru.art.platform.panel.constants.Migrations.USERS_MIGRATION
import ru.art.platform.panel.constants.UserActions.DEFAULT_USER_RIGHTS
import ru.art.platform.panel.constants.UserActions.GLOBAL_ADMINISTRATOR_ACTIONS
import ru.art.platform.panel.repository.AssemblyConfigurationRepository
import ru.art.platform.panel.repository.AssemblyConfigurationRepository.saveAssemblyConfiguration
import ru.art.platform.panel.repository.FilebeatApplicationRepository
import ru.art.platform.panel.repository.MigrationRepository.getMigrationStatus
import ru.art.platform.panel.repository.MigrationRepository.putMigrationStatus
import ru.art.platform.panel.repository.ModuleRepository.putModule
import ru.art.platform.panel.repository.ProjectRepository
import ru.art.platform.panel.repository.ProjectRepository.getProjects
import ru.art.platform.panel.repository.UserRepository
import ru.art.platform.panel.repository.UserRepository.putUser
import ru.art.tarantool.dao.TarantoolDao.tarantool

object MigrationService {
    fun migrate() {

    }
}
