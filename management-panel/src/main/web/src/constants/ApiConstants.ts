export const DEFAULT_RSOCKET_CLIENT_NAME = "Default RSocket client";

export const RSOCKET_DEFAULT_PORT = 10001;
// @ts-ignore
const rsocketProtocolHtml = document.querySelector("meta[name='rsocketProtocol']")?.content;
export const RSOCKET_PROTOCOL = process.env.RSOCKET_PROTOCOL
    ? process.env.RSOCKET_PROTOCOL
    : rsocketProtocolHtml && rsocketProtocolHtml != "{{rsocketProtocol}}"
        ? rsocketProtocolHtml
        : "ws";
// @ts-ignore
const rsocketHostHtml = document.querySelector("meta[name='rsocketHost']")?.content;
export const RSOCKET_HOST = process.env.RSOCKET_HOST
    ? process.env.RSOCKET_HOST
    : rsocketHostHtml && rsocketHostHtml != "{{rsocketHost}}"
        ? rsocketHostHtml
        : window.location.hostname;
// @ts-ignore
const rsocketPortHtml = document.querySelector("meta[name='rsocketPort']")?.content;
export const RSOCKET_PORT = process.env.RSOCKET_PORT
    ? process.env.RSOCKET_PORT
    : rsocketPortHtml && rsocketPortHtml != "{{rsocketPort}}"
        ? rsocketPortHtml
        : RSOCKET_DEFAULT_PORT;
export const RSOCKET_DEFAULT_URL = `${RSOCKET_PROTOCOL}://${RSOCKET_HOST}:${RSOCKET_PORT}`;
export const RSOCKET_REQUEST_COUNT = 9007199254740991;

export const RSOCKET_OPTIONS = {
    dataMimeType: 'application/message-pack',
    metadataMimeType: 'application/message-pack',
    keepAlive: 10 * 1000,
    lifetime: 60 * 1000
};

export const RSOCKET_FUNCTION = 'RSOCKET_FUNCTION_SERVICE';

export const REGISTER_USER = 'registerUser';
export const AUTHORIZE = 'authorize';
export const AUTHENTICATE = 'authenticate';
export const GET_USER_NAMES = 'getUserNames';
export const GET_USER = 'getUser';
export const GET_USERS = 'getUsers';
export const GET_USER_EMAILS = 'getUserEmails';
export const UPDATE_USER = 'updateUser';
export const DELETE_USER = 'deleteUser';
export const SUBSCRIBE_ON_USER = 'subscribeOnUser';

export const ADD_OPEN_SHIFT_RESOURCE = 'addOpenShiftResource';
export const ADD_ARTIFACTS_RESOURCE = 'addArtifactsResource';
export const ADD_GIT_RESOURCE = 'addGitResource';
export const ADD_PROXY_RESOURCE = 'addProxyResource';
export const GET_OPEN_SHIFT_RESOURCE = 'getOpenShiftResource';
export const GET_OPEN_SHIFT_RESOURCES = 'getOpenShiftResources';
export const GET_PROXY_RESOURCE = 'getProxyResource';
export const GET_PROXY_RESOURCES = 'getProxyResources';
export const GET_PLATFORM_RESOURCES = 'getPlatformResources';
export const GET_GIT_RESOURCE = 'getGitResource';
export const GET_GIT_RESOURCES = 'getGitResources';
export const GET_ARTIFACTS_RESOURCE = 'getArtifactsResource';
export const GET_ARTIFACTS_RESOURCES = 'getArtifactsResources';
export const GET_RESOURCES_IDS = 'getResourceIds';
export const UPDATE_GIT_RESOURCE = 'updateGitResource';
export const UPDATE_OPEN_SHIFT_RESOURCE = 'updateOpenShiftResource';
export const UPDATE_ARTIFACTS_RESOURCE = 'updateArtifactsResource';
export const UPDATE_PROXY_RESOURCE = 'updateProxyResource';
export const DELETE_GIT_RESOURCE = 'deleteGitResource';
export const DELETE_OPEN_SHIFT_RESOURCE = 'deleteOpenShiftResource';
export const DELETE_ARTIFACTS_RESOURCE = 'deleteArtifactsResource';
export const DELETE_PROXY_RESOURCE = 'deleteProxyResource';

export const ADD_PROJECT = 'addProject';
export const UPDATE_PROJECT = 'updateProject';
export const DELETE_PROJECT = 'deleteProject';
export const ADD_EXTERNAL_ARTIFACTS = 'addExternalArtifacts';
export const GET_PROJECTS = 'getProjects';
export const GET_ASSEMBLED_PROJECT_ARTIFACTS = 'getAssembledProjectArtifacts';
export const GET_PROJECT = 'getProject';
export const RELOAD_PROJECT = 'reloadProject';
export const SUBSCRIBE_ON_PROJECT = 'subscribeOnProject';

export const BUILD_PROJECT = 'buildProject';
export const REBUILD_PROJECT = 'rebuildProject';
export const GET_ASSEMBLIES = 'getAssemblies';
export const SUBSCRIBE_ON_ASSEMBLY = 'subscribeOnAssembly';
export const SAVE_ASSEMBLY_CONFIGURATION = 'saveAssemblyConfiguration';
export const GET_ASSEMBLY_CONFIGURATION = 'getAssemblyConfiguration';
export const GET_ASSEMBLY = 'getAssembly';
export const DELETE_ASSEMBLY = 'deleteAssembly';
export const CANCEL_ASSEMBLY = 'cancelAssembly';
export const GET_FILTERED_ASSEMBLIES = "getFilteredAssemblies";

export const ADD_LOG_RECORD = "addLogRecord";
export const UPDATE_LOG = "updateLog";
export const DELETE_LOG = "deleteLog";
export const GET_LOG = "getLog";
export const GET_LOGS = "getLogs";
export const SUBSCRIBE_ON_LOG = "subscribeOnLog";

export const START_MODULE_INSTALLATION = 'startModuleInstallation';
export const PROCESS_MODULE_INSTALLATION = 'processModuleInstallation';
export const START_MODULE_UPDATING = 'startModuleUpdating';
export const PROCESS_MODULE_UPDATING = "processModuleUpdating";
export const REFRESH_MODULE_ARTIFACT = "refreshModuleArtifact";
export const UPDATE_MODULES_VERSION = "updateModulesVersion";
export const STOP_MODULE = 'stopModule';
export const DELETE_MODULE_FROM_RESOURCE = 'deleteModuleFromResource';
export const DELETE_MODULE = 'deleteModule';
export const GET_MODULE = 'getModule';
export const GET_MODULES = 'getModules';
export const SUBSCRIBE_ON_MODULE = 'subscribeOnModule';
export const REINSTALL_MODULE = 'reinstallModule';
export const RESTART_MODULE = 'restartModule';
export const GET_FILTERED_MODULES = "getFilteredModules";

export const CHECK_NETWORK_ACCESS = 'checkNetworkAccess';

export const ALLOCATE_FILE = 'allocateFile';
export const UPLOAD_FILE = 'uploadFile';
export const GET_FILE = 'getFile';
export const CLONE_FILE = 'cloneFile';

export const START_LOAD_TEST = "startLoadTest";
export const CANCEL_LOAD_TEST = "cancelLoadTest";
export const SAVE_LOAD_TEST_SCENARIO = "saveLoadTestScenario";
export const UPDATE_LOAD_TEST_SCENARIO = "updateLoadTestScenario";
export const GET_LOAD_TEST_SCENARIO = "getLoadTestScenario";
export const GET_PROJECTS_LOAD_TEST_SCENARIOS = "getProjectsLoadTestScenarios";
export const GET_PROJECTS_LOAD_TESTS = "getProjectsLoadTests";
export const GET_LOAD_TEST = "getLoadTest";
export const DELETE_LOAD_TEST_SCENARIO = "deleteLoadTestScenario";
export const SUBSCRIBE_ON_LOAD_TEST = "subscribeOnLoadTest";
export const DELETE_LOAD_TEST = "deleteLoadTest";

export const ADD_FILEBEAT_APPLICATION = 'addFilebeatApplication';
export const GET_FILEBEAT_APPLICATION = 'getFilebeatApplication';
export const GET_FILEBEAT_APPLICATIONS = 'getFilebeatApplications';
export const GET_APPLICATIONS_IDS = 'getApplicationIds';
export const UPDATE_FILEBEAT_APPLICATION = 'updateFilebeatApplication';
export const DELETE_FILEBEAT_APPLICATION = 'deleteFilebeatApplication';

export const GET_VERSION = 'getVersion';

export const GET_PREPARED_CONFIGURATION = "getPreparedConfiguration"
export const GET_PREPARED_CONFIGURATIONS = "getPreparedConfigurations"
export const GET_FILTERED_PREPARED_CONFIGURATIONS = "getFilteredPreparedConfigurations"
export const ADD_PREPARED_CONFIGURATION = "addPreparedConfiguration"
export const UPDATE_PREPARED_CONFIGURATION = "updatePreparedConfiguration"
export const DELETE_PREPARED_CONFIGURATION = "deletePreparedConfiguration"
export const GET_PREPARED_CONFIGURATION_IDS = "getPreparedConfigurationIds"

export const RETRY_TIMEOUT_SECONDS = 10;

export const apiLogsEnabled = () => false;
