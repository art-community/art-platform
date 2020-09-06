package ru.art.platform.panel.configurator

import ru.art.platform.common.constants.PlatformKeywords.*
import ru.art.platform.panel.constants.DbConstants.APPLICATION_NAME_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.APPLICATION_NAME_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.ARTIFACTS_RESOURCE_SPACE
import ru.art.platform.panel.constants.DbConstants.ASSEMBLY_PROJECT_ID_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.ASSEMBLY_PROJECT_ID_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.PREPARED_CONFIGURATION_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.EMAIL_INDEX_NAME
import ru.art.platform.panel.constants.DbConstants.FILEBEAT_APPLICATION_SPACE
import ru.art.platform.panel.constants.DbConstants.GIT_RESOURCE_SPACE
import ru.art.platform.panel.constants.DbConstants.LOAD_TEST_PROJECT_ID_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.LOAD_TEST_PROJECT_ID_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.LOAD_TEST_SCENARIO_PROJECT_ID_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.LOAD_TEST_SCENARIO_PROJECT_ID_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.MIGRATION_NAME_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.MIGRATION_NAME_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.MODULE_NAME_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.MODULE_NAME_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.MODULE_PROJECT_ID_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.MODULE_PROJECT_ID_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.OPEN_SHIFT_RESOURCE_SPACE
import ru.art.platform.panel.constants.DbConstants.PREPARED_CONFIGURATIONS_SPACE
import ru.art.platform.panel.constants.DbConstants.PREPARED_CONFIGURATION_INDEX_NAME
import ru.art.platform.panel.constants.DbConstants.PREPARED_CONFIGURATION_NAME_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.PREPARED_CONFIGURATION_PROFILE_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.PREPARED_CONFIGURATION_PROJECT_ID_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.PROJECT_ID_INDEX_NAME
import ru.art.platform.panel.constants.DbConstants.PROJECT_NAME_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.PROJECT_NAME_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.PROXY_RESOURCE_SPACE
import ru.art.platform.panel.constants.DbConstants.RESOURCE_NAME_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.RESOURCE_NAME_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.TOKEN_TOKEN_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.TOKEN_TOKEN_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.USER_EMAIL_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.USER_EMAIL_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.USER_NAME_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.USER_NAME_INDEX_ID
import ru.art.platform.panel.constants.DbConstants.USER_TOKEN_FIELD_NUM
import ru.art.platform.panel.constants.DbConstants.USER_TOKEN_INDEX_ID
import ru.art.tarantool.configuration.lua.TarantoolIndexConfiguration
import ru.art.tarantool.constants.TarantoolModuleConstants.TarantoolFieldType.*
import ru.art.tarantool.service.TarantoolIndexService.createIndex


fun configureTarantool() {
    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(USER_NAME_INDEX_ID)
            .spaceName(USER_CAMEL_CASE)
            .indexName(NAME_CAMEL_CASE)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(USER_NAME_FIELD_NUM).type(STRING).build())
            .build())
    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(USER_EMAIL_INDEX_ID)
            .spaceName(USER_CAMEL_CASE)
            .indexName(EMAIL_INDEX_NAME)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(USER_EMAIL_FIELD_NUM).type(STRING).build())
            .build())
    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(USER_TOKEN_INDEX_ID)
            .spaceName(USER_CAMEL_CASE)
            .indexName(TOKEN_CAMEL_CASE)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(USER_TOKEN_FIELD_NUM).type(STRING).build())
            .build())
    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(TOKEN_TOKEN_INDEX_ID)
            .spaceName(TOKEN_CAMEL_CASE)
            .indexName(TOKEN_CAMEL_CASE)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(TOKEN_TOKEN_FIELD_NUM).type(STRING).build())
            .build())

    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(RESOURCE_NAME_INDEX_ID)
            .spaceName(OPEN_SHIFT_RESOURCE_SPACE)
            .indexName(NAME_CAMEL_CASE)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(RESOURCE_NAME_FIELD_NUM).type(STRING).build())
            .build())
    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(RESOURCE_NAME_INDEX_ID)
            .spaceName(ARTIFACTS_RESOURCE_SPACE)
            .indexName(NAME_CAMEL_CASE)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(RESOURCE_NAME_FIELD_NUM).type(STRING).build())
            .build())
    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(RESOURCE_NAME_INDEX_ID)
            .spaceName(PROXY_RESOURCE_SPACE)
            .indexName(NAME_CAMEL_CASE)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(RESOURCE_NAME_FIELD_NUM).type(STRING).build())
            .build())
    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(RESOURCE_NAME_INDEX_ID)
            .spaceName(GIT_RESOURCE_SPACE)
            .indexName(NAME_CAMEL_CASE)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(RESOURCE_NAME_FIELD_NUM).type(STRING).build())
            .build())

    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(PROJECT_NAME_INDEX_ID)
            .spaceName(PROJECT_CAMEL_CASE)
            .indexName(NAME_CAMEL_CASE)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(PROJECT_NAME_FIELD_NUM).type(STRING).build())
            .build())

    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(ASSEMBLY_PROJECT_ID_INDEX_ID)
            .spaceName(ASSEMBLY_CAMEL_CASE)
            .indexName(PROJECT_ID_INDEX_NAME)
            .unique(false)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(ASSEMBLY_PROJECT_ID_FIELD_NUM).type(NUMBER).build())
            .build())

    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(MODULE_NAME_INDEX_ID)
            .spaceName(MODULE_CAMEL_CASE)
            .indexName(NAME_CAMEL_CASE)
            .unique(false)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(MODULE_NAME_FIELD_NUM).type(STRING).build())
            .build())
    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(MODULE_PROJECT_ID_INDEX_ID)
            .spaceName(MODULE_CAMEL_CASE)
            .indexName(PROJECT_ID_INDEX_NAME)
            .unique(false)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(MODULE_PROJECT_ID_FIELD_NUM).build())
            .build())

    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(LOAD_TEST_SCENARIO_PROJECT_ID_INDEX_ID)
            .spaceName(LOAD_TEST_SCENARIO_CAMEL_CASE)
            .indexName(PROJECT_ID_INDEX_NAME)
            .unique(false)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(LOAD_TEST_SCENARIO_PROJECT_ID_FIELD_NUM).build())
            .build())
    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(LOAD_TEST_PROJECT_ID_INDEX_ID)
            .spaceName(LOAD_TEST_CAMEL_CASE)
            .indexName(PROJECT_ID_INDEX_NAME)
            .unique(false)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(LOAD_TEST_PROJECT_ID_FIELD_NUM).build())
            .build())

    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(APPLICATION_NAME_INDEX_ID)
            .spaceName(FILEBEAT_APPLICATION_SPACE)
            .indexName(NAME_CAMEL_CASE)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(APPLICATION_NAME_FIELD_NUM).type(STRING).build())
            .build())

    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(MIGRATION_NAME_INDEX_ID)
            .spaceName(MIGRATION_CAMEL_CASE)
            .indexName(NAME_CAMEL_CASE)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(MIGRATION_NAME_FIELD_NUM).type(STRING).build())
            .build())

    createIndex(PLATFORM_CAMEL_CASE, TarantoolIndexConfiguration.builder()
            .id(PREPARED_CONFIGURATION_INDEX_ID)
            .spaceName(PREPARED_CONFIGURATIONS_SPACE)
            .indexName(PREPARED_CONFIGURATION_INDEX_NAME)
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(PREPARED_CONFIGURATION_PROJECT_ID_FIELD_NUM).type(UNSIGNED).build())
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(PREPARED_CONFIGURATION_PROFILE_FIELD_NUM).type(STRING).build())
            .part(TarantoolIndexConfiguration.Part.builder().fieldNumber(PREPARED_CONFIGURATION_NAME_FIELD_NUM).type(STRING).build())
            .build())
}
