import {createFunctionRequest, fireAndForget, infinityRequestStream, requestResponse} from "../client/PlatformClient";
import {
    DELETE_MODULE,
    DELETE_MODULE_FROM_RESOURCE,
    GET_FILTERED_MODULES,
    GET_MODULE,
    GET_MODULES,
    PROCESS_MODULE_INSTALLATION,
    PROCESS_MODULE_UPDATING,
    REFRESH_MODULE_ARTIFACT,
    REINSTALL_MODULE,
    RESTART_MODULE,
    START_MODULE_INSTALLATION,
    START_MODULE_UPDATING,
    STOP_MODULE,
    SUBSCRIBE_ON_MODULE,
    UPDATE_MODULES_VERSION
} from "../constants/ApiConstants";
import {Dispatch} from "react";
import {useApiActions} from "./ApiActions";
import {StreamEvent} from "../framework/pattern/Stream";
import {Module, ModuleFilterCriteria, ModuleInformation, ModuleInstallationRequest, ModuleUpdateRequest, UpdateModulesVersionRequest} from "../model/ModuleTypes";
import {platform} from "../component/entry/EntryPoint";

export const useModuleApi = () => {
    const actions = useApiActions();

    return {
        startModuleInstallation: (request: ModuleInstallationRequest, onComplete: Dispatch<Module>) => {
            requestResponse(createFunctionRequest(START_MODULE_INSTALLATION, request), onComplete, actions.errorHandler())
        },
        processModuleInstallation: (request: Module) => {
            fireAndForget(createFunctionRequest(PROCESS_MODULE_INSTALLATION, request))
        },
        reinstallModule: (id: number) => {
            fireAndForget(createFunctionRequest(REINSTALL_MODULE, id))
        },
        getModule: (request: number, onComplete: Dispatch<Module>) => {
            requestResponse(createFunctionRequest(GET_MODULE, request), onComplete, actions.errorHandler())
        },
        getModules: (onComplete: Dispatch<Module[]>) => {
            requestResponse(
                createFunctionRequest(GET_MODULES),
                modules => onComplete(modules.filter(module => platform.user()?.availableProjects?.includes(module.projectId))),
                actions.errorHandler()
            )
        },
        getFilteredModules: (request: ModuleFilterCriteria, onComplete: Dispatch<ModuleInformation[]>) => {
            requestResponse(
                createFunctionRequest(GET_FILTERED_MODULES, request),
                modules => onComplete(modules.filter(module => platform.user()?.admin || platform.user()?.availableProjects?.includes(module.projectId))),
                actions.errorHandler()
            )
        },
        startModuleUpdating: (request: ModuleUpdateRequest, onComplete: Dispatch<Module>) => {
            requestResponse(createFunctionRequest(START_MODULE_UPDATING, request), onComplete, actions.errorHandler())
        },
        processModuleUpdating: (request: Module) => {
            fireAndForget(createFunctionRequest(PROCESS_MODULE_UPDATING, request))
        },
        refreshModuleArtifact: (request: number) => {
            fireAndForget(createFunctionRequest(REFRESH_MODULE_ARTIFACT, request))
        },
        updateModulesVersion: (request: UpdateModulesVersionRequest) => {
            fireAndForget(createFunctionRequest(UPDATE_MODULES_VERSION, request))
        },
        stopModule: (id: number) => {
            fireAndForget(createFunctionRequest(STOP_MODULE, id))
        },
        restartModule: (id: number) => {
            fireAndForget(createFunctionRequest(RESTART_MODULE, id))
        },
        deleteModuleFromResource: (id: number) => {
            fireAndForget(createFunctionRequest(DELETE_MODULE_FROM_RESOURCE, id))
        },
        deleteModule: (id: number) => {
            requestResponse(createFunctionRequest(DELETE_MODULE, id))
        },
        subscribeOnModule: (onUpdate: Dispatch<StreamEvent<Module>>) => {
            return infinityRequestStream(
                createFunctionRequest(SUBSCRIBE_ON_MODULE),
                event => (platform.user()?.admin || platform.user()?.availableProjects?.includes(event.data.projectId)) && onUpdate(event),
                actions.errorHandler
            );
        },
    };
};
