package ru.art.platform.constants

import ru.art.config.extensions.ConfigExtensions.configStringList
import ru.art.platform.common.constants.PlatformKeywords.PLATFORM_CAMEL_CASE
import ru.art.platform.constants.ConfigKeys.USER_DEFAULT_ACTIONS_KEY

object WebConstants {
    const val RSOCKET_PROTOCOL_VARIABLE = "rsocketProtocol"
    const val RSOCKET_HOST_VARIABLE = "rsocketHost"
    const val RSOCKET_PORT_VARIABLE = "rsocketPort"
    const val EXTERNAL_RSOCKET_PROTOCOL_SYSTEM_ENV = "EXTERNAL_RSOCKET_PROTOCOL"
    const val EXTERNAL_RSOCKET_HOST_SYSTEM_ENV = "EXTERNAL_RSOCKET_HOST"
    const val EXTERNAL_RSOCKET_PORT_SYSTEM_ENV = "EXTERNAL_RSOCKET_PORT"
}

object CommonConstants {
    const val TOKEN_LIFE_TIME_DAYS = 30
    const val VERSION_SYSTEM_ENV = "VERSION"
    const val USE_LEGACY_MERGE_SORT = "java.util.Arrays.useLegacyMergeSort"
    const val DEFAULT_AGENT_TIMEOUT = 5L
}

object DbConstants {
    const val EXTERNAL_ARTIFACT_SPACE = "externalArtifact"
    const val OPEN_SHIFT_RESOURCE_SPACE = "openShiftResource"
    const val PLATFORM_RESOURCE_SPACE = "platformResource"
    const val ARTIFACTS_RESOURCE_SPACE = "artifactsResource"
    const val PREPARED_CONFIGURATIONS_SPACE = "preparedConfigurationsSpace"
    const val PROXY_RESOURCE_SPACE = "proxyResource"
    const val GIT_RESOURCE_SPACE = "gitResource"
    const val ASSEMBLY_CONFIGURATION_SPACE = "assemblyConfiguration"
    const val PLATFORM_FILE_META_DATA_SPACE = "platformFileMetaData"
    const val PLATFORM_FILE_DATA_SPACE = "platformFileData"
    const val FILEBEAT_APPLICATION_SPACE = "filebeatApplication"

    const val PROJECT_ID_INDEX_NAME = "projectId"
    const val EMAIL_INDEX_NAME = "email"
    const val PREPARED_CONFIGURATION_INDEX_NAME = "preparedConfigurationIndex"

    const val USER_NAME_INDEX_ID = 1
    const val USER_NAME_FIELD_NUM = 2

    const val USER_TOKEN_INDEX_ID = 2
    const val USER_TOKEN_FIELD_NUM = 3

    const val USER_EMAIL_INDEX_ID = 3
    const val USER_EMAIL_FIELD_NUM = 6

    const val TOKEN_TOKEN_INDEX_ID = 1
    const val TOKEN_TOKEN_FIELD_NUM = 2

    const val RESOURCE_NAME_INDEX_ID = 1
    const val RESOURCE_NAME_FIELD_NUM = 2

    const val PROJECT_NAME_INDEX_ID = 1
    const val PROJECT_NAME_FIELD_NUM = 2

    const val ASSEMBLY_PROJECT_ID_INDEX_ID = 1
    const val ASSEMBLY_PROJECT_ID_FIELD_NUM = 2

    const val MODULE_NAME_INDEX_ID = 1
    const val MODULE_NAME_FIELD_NUM = 2

    const val MODULE_PROJECT_ID_INDEX_ID = 2
    const val MODULE_PROJECT_ID_FIELD_NUM = 3

    const val LOAD_TEST_SCENARIO_PROJECT_ID_INDEX_ID = 1
    const val LOAD_TEST_SCENARIO_PROJECT_ID_FIELD_NUM = 2

    const val LOAD_TEST_PROJECT_ID_INDEX_ID = 1
    const val LOAD_TEST_PROJECT_ID_FIELD_NUM = 2

    const val APPLICATION_NAME_INDEX_ID = 1
    const val APPLICATION_NAME_FIELD_NUM = 2

    const val MIGRATION_NAME_INDEX_ID = 1
    const val MIGRATION_NAME_FIELD_NUM = 2

    const val PREPARED_CONFIGURATION_INDEX_ID = 1
    const val PREPARED_CONFIGURATION_PROJECT_ID_FIELD_NUM = 2
    const val PREPARED_CONFIGURATION_PROFILE_FIELD_NUM = 3
    const val PREPARED_CONFIGURATION_NAME_FIELD_NUM = 4
}

object ServiceConstants {
    const val REGISTER_USER = "registerUser"
    const val AUTHORIZE = "authorize"
    const val AUTHENTICATE = "authenticate"
    const val GET_USER = "getUser"
    const val GET_USERS = "getUsers"
    const val GET_USER_NAMES = "getUserNames"
    const val GET_USER_EMAILS = "getUserEmails"
    const val UPDATE_USER = "updateUser"
    const val DELETE_USER = "deleteUser"
    const val SUBSCRIBE_ON_USER = "subscribeOnUser"

    const val ADD_OPEN_SHIFT_RESOURCE = "addOpenShiftResource"
    const val ADD_ARTIFACTS_RESOURCE = "addArtifactsResource"
    const val ADD_GIT_RESOURCE = "addGitResource"
    const val ADD_PROXY_RESOURCE = "addProxyResource"

    const val UPDATE_OPEN_SHIFT_RESOURCE = "updateOpenShiftResource"
    const val UPDATE_ARTIFACTS_RESOURCE = "updateArtifactsResource"
    const val UPDATE_GIT_RESOURCE = "updateGitResource"
    const val UPDATE_PROXY_RESOURCE = "updateProxyResource"

    const val GET_OPEN_SHIFT_RESOURCE = "getOpenShiftResource"
    const val GET_OPEN_SHIFT_RESOURCES = "getOpenShiftResources"
    const val GET_PLATFORM_RESOURCES = "getPlatformResources"
    const val GET_ARTIFACTS_RESOURCE = "getArtifactsResource"
    const val GET_ARTIFACTS_RESOURCES = "getArtifactsResources"
    const val GET_GIT_RESOURCE = "getGitResource"
    const val GET_GIT_RESOURCES = "getGitResources"
    const val GET_PROXY_RESOURCE = "getProxyResource"
    const val GET_PROXY_RESOURCES = "getProxyResources"

    const val GET_RESOURCE_IDS = "getResourceIds"

    const val DELETE_OPEN_SHIFT_RESOURCE = "deleteOpenShiftResource"
    const val DELETE_ARTIFACTS_RESOURCE = "deleteArtifactsResource"
    const val DELETE_GIT_RESOURCE = "deleteGitResource"
    const val DELETE_PROXY_RESOURCE = "deleteProxyResource"

    const val ADD_PROJECT = "addProject"
    const val RELOAD_PROJECT = "reloadProject"
    const val UPDATE_PROJECT = "updateProject"
    const val GET_PROJECTS = "getProjects"
    const val GET_INITIALIZED_PROJECTS = "getInitializedProjects"
    const val GET_ASSEMBLED_PROJECT_ARTIFACTS = "getAssembledProjectArtifacts"
    const val GET_PROJECT = "getProject"
    const val DELETE_PROJECT = "deleteProject"
    const val ADD_EXTERNAL_ARTIFACTS = "addExternalArtifacts"
    const val SUBSCRIBE_ON_PROJECT = "subscribeOnProject"

    const val BUILD_PROJECT = "buildProject"
    const val GET_LATEST_ASSEMBLED_ARTIFACTS = "getLatestAssembledArtifacts"
    const val REBUILD_PROJECT = "rebuildProject"
    const val GET_ASSEMBLY = "getAssembly"
    const val CANCEL_ASSEMBLY = "cancelAssembly"
    const val DELETE_ASSEMBLY = "deleteAssembly"
    const val GET_ASSEMBLIES = "getAssemblies"
    const val SUBSCRIBE_ON_ASSEMBLY = "subscribeOnAssembly"
    const val SAVE_ASSEMBLY_CONFIGURATION = "saveAssemblyConfiguration"
    const val GET_ASSEMBLY_CONFIGURATION = "getAssemblyConfiguration"
    const val GET_FILTERED_ASSEMBLIES = "getFilteredAssemblies"

    const val ADD_LOG_RECORD = "addLogRecord"
    const val UPDATE_LOG = "updateLog"
    const val DELETE_LOG = "deleteLog"
    const val GET_LOG = "getLog"
    const val GET_LOGS = "getLogs"
    const val SUBSCRIBE_ON_LOG = "subscribeOnLog"

    const val ALLOCATE_FILE = "allocateFile"
    const val PUT_FILE = "putFile"
    const val CLONE_FILE = "cloneFile"
    const val UPLOAD_FILE = "uploadFile"
    const val GET_FILE = "getFile"

    const val START_MODULE_INSTALLATION = "startModuleInstallation"
    const val PROCESS_MODULE_INSTALLATION = "processModuleInstallation"
    const val REINSTALL_MODULE = "reinstallModule"
    const val START_MODULE_UPDATING = "startModuleUpdating"
    const val PROCESS_MODULE_UPDATING = "processModuleUpdating"
    const val REFRESH_MODULE_ARTIFACT = "refreshModuleArtifact"
    const val STOP_MODULE = "stopModule"
    const val RESTART_MODULE = "restartModule"
    const val DELETE_MODULE_FROM_RESOURCE = "deleteModuleFromResource"
    const val GET_MODULE = "getModule"
    const val GET_MODULES = "getModules"
    const val DELETE_MODULE = "deleteModule"
    const val GET_PROJECT_MODULES = "getProjectModules"
    const val SUBSCRIBE_ON_MODULE = "subscribeOnModule"
    const val GET_FILTERED_MODULES = "getFilteredModules"
    const val UPDATE_MODULES_VERSION = "updateModulesVersion"

    const val CHECK_NETWORK_ACCESS = "checkNetworkAccess"

    const val START_LOAD_TEST = "startLoadTest"
    const val CANCEL_LOAD_TEST = "cancelLoadTest"
    const val GET_LOAD_TEST = "getLoadTest"
    const val SAVE_LOAD_TEST_SCENARIO = "saveLoadTestScenario"
    const val UPDATE_LOAD_TEST_SCENARIO = "updateLoadTestScenario"
    const val GET_LOAD_TEST_SCENARIO = "getLoadTestScenario"
    const val GET_PROJECTS_LOAD_TEST_SCENARIOS = "getProjectsLoadTestScenarios"
    const val GET_PROJECTS_LOAD_TESTS = "getProjectsLoadTests"
    const val DELETE_LOAD_TEST_SCENARIO = "deleteLoadTestScenario"
    const val SUBSCRIBE_ON_LOAD_TEST = "subscribeOnLoadTest"
    const val DELETE_LOAD_TEST = "deleteLoadTest"

    const val ADD_FILEBEAT_APPLICATION = "addFilebeatApplication"
    const val GET_FILEBEAT_APPLICATION = "getFilebeatApplication"
    const val GET_FILEBEAT_APPLICATIONS = "getFilebeatApplications"
    const val UPDATE_FILEBEAT_APPLICATION = "updateFilebeatApplication"
    const val DELETE_FILEBEAT_APPLICATION = "deleteFilebeatApplication"
    const val GET_APPLICATION_IDS = "getApplicationIds"

    const val ON_EVENT = "onEvent"

    const val BIT_BUCKET = "bitBucket"
    const val BACKUP = "backup"

    const val GET_VERSION = "getVersion"
    const val GET_UI_ACTIONS = "getUiActions"

    const val GET_PREPARED_CONFIGURATION = "getPreparedConfiguration"
    const val GET_PREPARED_CONFIGURATIONS = "getPreparedConfigurations"
    const val GET_FILTERED_PREPARED_CONFIGURATIONS = "getFilteredPreparedConfigurations"
    const val ADD_PREPARED_CONFIGURATION = "addPreparedConfiguration"
    const val UPDATE_PREPARED_CONFIGURATION = "updatePreparedConfiguration"
    const val DELETE_PREPARED_CONFIGURATION = "deletePreparedConfiguration"
    const val GET_PREPARED_CONFIGURATION_IDS = "getPreparedConfigurationIds"
}

object OpenShiftConstants {
    const val PLATFORM_PROJECT_INITIALIZER = "platform-project-initializer"
    const val PLATFORM_MODULE_MANAGER = "platform-module-manager"
    const val PLATFORM_PROJECT_BUILDER = "platform-project-builder"
    const val PLATFORM_ASSEMBLY_GRADLE_CACHE = "platform-assembly-gradle-cache"
    const val PLATFORM_NETWORK_ACCESS_CHECKER = "platform-network-access-checker"
    const val PLATFORM_LOAD_TEST_RUNNER = "platform-load-test-runner"
}

object RsocketConstants {
    const val FILE_STREAM_CHUNKS_COUNT = 256
    const val AGENT_STREAM_CHUNKS_COUNT = 512
}

object BitBucketConstants {
    const val REPOSITORY_PROJECT_KEY = "repository.project.key"
    const val REPOSITORY_NAME = "repository.name"
    const val CHANGES = "changes"
    const val REF_ID = "refId"
    const val FROM_HASH = "fromHash"
    const val TO_HASH = "toHash"
}

object ConfigKeys {
    const val SECRET_KEY = "platform.secret"
    const val OPEN_SHIFT_PLATFORM_PROJECT_NAME_KEY = "$PLATFORM_CAMEL_CASE.openShift.platformProjectName"
    const val USER_ADMINISTRATOR_NAME_KEY = "$PLATFORM_CAMEL_CASE.user.administrator.name"
    const val USER_ADMINISTRATOR_PASSWORD_KEY = "$PLATFORM_CAMEL_CASE.user.administrator.password"
    const val AGENT_IMAGE_KEY = "$PLATFORM_CAMEL_CASE.agent.image"
    const val AGENT_BUILDER_PROJECT_NAME_KEY = "$PLATFORM_CAMEL_CASE.agent.builderProjectName"
    const val AGENT_MODULE_MANAGER_PROJECT_NAME_KEY = "$PLATFORM_CAMEL_CASE.agent.moduleManagerProjectName"
    const val AGENT_PROJECT_INITIALIZER_PROJECT_NAME_KEY = "$PLATFORM_CAMEL_CASE.agent.projectInitializerProjectName"
    const val AGENT_TIMEOUT_MINUTES = "$PLATFORM_CAMEL_CASE.agent.timeoutMinutes"
    const val ENABLE_SERVICE_LOGGING_KEY = "$PLATFORM_CAMEL_CASE.enableServiceLogging"
    const val USER_DEFAULT_ACTIONS_KEY = "$PLATFORM_CAMEL_CASE.user.defaultActions"
    const val RESOURCES_SECTION = "resources"
    const val ID_KEY = "id"
    const val NAME_KEY = "name"
    const val URL_KEY = "url"
    const val USER_NAME_KEY = "userName"
    const val PASSWORD_KEY = "password"
    const val SSL_ENABLED_KEY = "ssl.enabled"
    const val SSL_KEY_PATH_KEY = "$PLATFORM_CAMEL_CASE.ssl.key"
    const val SSL_CERT_PATH_KEY = "$PLATFORM_CAMEL_CASE.ssl.cert"
    const val SSL_STORE_PASSWORD = "$PLATFORM_CAMEL_CASE.ssl.password"
}

object SecurityConstants {
    const val KEY_FILE_NAME = "platform-security.key"
}

object UserActions {
    const val PROJECTS_MANAGEMENT = "PROJECTS_MANAGEMENT"
    const val ASSEMBLIES_MANAGEMENT = "ASSEMBLIES_MANAGEMENT"
    const val RESOURCES_MANAGEMENT = "RESOURCES_MANAGEMENT"
    const val MODULES_MANAGEMENT = "MODULES_MANAGEMENT"
    const val CONFIGURATIONS_MANAGEMENT = "CONFIGURATIONS_MANAGEMENT"
    const val NETWORK_ACCESSES_CHECKING = "NETWORK_ACCESSES_CHECKING"
    const val LOAD_TESTS_MANAGEMENT = "LOAD_TESTS_MANAGEMENT"
    const val APPLICATIONS_MANAGEMENT = "APPLICATIONS_MANAGEMENT"
    const val ADMINISTRATION = "ADMINISTRATION"

    val GLOBAL_ADMINISTRATOR_ACTIONS = setOf(
            CONFIGURATIONS_MANAGEMENT,
            PROJECTS_MANAGEMENT,
            ASSEMBLIES_MANAGEMENT,
            RESOURCES_MANAGEMENT,
            MODULES_MANAGEMENT,
            NETWORK_ACCESSES_CHECKING,
            LOAD_TESTS_MANAGEMENT,
            APPLICATIONS_MANAGEMENT,
            ADMINISTRATION
    )

    val DEFAULT_USER_RIGHTS = configStringList(USER_DEFAULT_ACTIONS_KEY).toSet()
}

object MigrationStatus {
    const val PROCESSED = "processed"
}

object Migrations {
    const val MODULES_MIGRATION = "MODULES_MIGRATION"
}
