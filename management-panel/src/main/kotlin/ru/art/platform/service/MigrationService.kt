package ru.art.platform.service

import ru.art.platform.api.mapping.assembly.AssembledArtifactMapper
import ru.art.platform.api.mapping.external.ExternalIdentifierMapper
import ru.art.platform.api.mapping.resource.ResourceIdentifierMapper
import ru.art.platform.api.model.external.PortMapping
import ru.art.platform.api.model.file.StringFile
import ru.art.platform.api.model.module.Module
import ru.art.platform.api.model.module.ModuleUrl
import ru.art.platform.common.constants.PlatformKeywords.MODULE_CAMEL_CASE
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.constants.MigrationStatus.PROCESSED
import ru.art.platform.constants.Migrations.MODULES_MIGRATION
import ru.art.platform.repository.MigrationRepository.getMigrationStatus
import ru.art.platform.repository.ModuleRepository.putModule
import ru.art.tarantool.dao.TarantoolDao.tarantool

object MigrationService {
    fun migrateModules() {
        if (getMigrationStatus(MODULES_MIGRATION).filter { status -> status == PROCESSED }.isPresent) {
            return
        }
        tarantool(PLATFORM_CAMEL_CASE)
                .selectAll(MODULE_CAMEL_CASE)
                .forEach { module ->
                    val configuration = module.getString("configuration")
                    val configurationFileName = module.getString("configurationFileName")
                    val loggingConfiguration = module.getString("loggingConfiguration")
                    val loggingConfigurationFileName = module.getString("loggingConfigurationFileName")
                    val builder = Module.builder()
                            .id(module.getLong("id"))
                            .name(module.getString("name"))
                            .projectId(module.getLong("projectId"))
                            .artifact(module.getValue("artifact", AssembledArtifactMapper.toAssembledArtifact))
                            .count(module.getInt("count"))
                            .externalId(module.getValue("externalId", ExternalIdentifierMapper.toExternalIdentifier))
                            .resourceId(module.getValue("resourceId", ResourceIdentifierMapper.toResourceIdentifier))
                            .ports(module.getIntList("internalPorts"))
                            .portMappings(module.getEntityList("externalPorts").map { external ->
                                PortMapping.builder()
                                        .internalPort(external.getInt("internalPort"))
                                        .externalPort(external.getInt("externalPort"))
                                        .build()
                            })
                            .state(module.getString("state"))
                            .updateTimeStamp(module.getLong("updateTimeStamp"))

                    if (!module.getString("externalUrl").isNullOrBlank() && module.getInt("externalTargetPort") != null) {
                        builder.url(ModuleUrl.builder().url(module.getString("externalUrl")).port(module.getInt("externalTargetPort")).build())
                    }

                    if (!module.getString("launchParameters").isNullOrBlank()) {
                        builder.parameters(module.getString("launchParameters"))
                    }

                    if (!module.getString("launchParameters").isNullOrBlank()) {
                        builder.parameters(module.getString("launchParameters"))
                    }

                    if (!module.getString("configurationFileName").isNullOrBlank()) {
                        builder.manualConfiguration(StringFile.builder().name(configurationFileName).content(configuration).build())
                    }

                    if (!module.getString("loggingConfigurationFileName").isNullOrBlank()) {
                        builder.manualConfiguration(StringFile.builder().name(loggingConfigurationFileName).content(loggingConfiguration).build())
                    }

                    putModule(builder.build())
                }
    }
}
