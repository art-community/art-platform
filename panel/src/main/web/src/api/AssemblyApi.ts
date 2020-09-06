import {createFunctionRequest, infinityRequestStream, requestResponse} from "../client/PlatformClient";
import {
    BUILD_PROJECT,
    CANCEL_ASSEMBLY,
    DELETE_ASSEMBLY,
    GET_ASSEMBLIES,
    GET_ASSEMBLY,
    GET_ASSEMBLY_CONFIGURATION,
    GET_FILTERED_ASSEMBLIES,
    REBUILD_PROJECT,
    SAVE_ASSEMBLY_CONFIGURATION,
    SUBSCRIBE_ON_ASSEMBLY
} from "../constants/ApiConstants";
import {Dispatch, DispatchWithoutAction} from "react";
import {useApiActions} from "./ApiActions";
import {ServiceExceptionDispatch} from "../model/ApiTypes";
import {StreamEvent} from "../framework/pattern/Stream";
import {Assembly, AssemblyConfiguration, AssemblyFilterCriteria, AssemblyInformation, BuildRequest} from "../model/AssemblyTypes";
import {doNothing} from "../framework/constants/Constants";
import {platform} from "../component/entry/EntryPoint";


export const useAssemblyApi = () => {
    const actions = useApiActions();

    return {
        buildProject: (request: BuildRequest, onComplete: DispatchWithoutAction) => {
            requestResponse(createFunctionRequest(BUILD_PROJECT, request), onComplete, actions.errorHandler())
        },
        saveAssemblyConfiguration: (request: AssemblyConfiguration, onComplete: Dispatch<AssemblyConfiguration>, onError: ServiceExceptionDispatch) => {
            requestResponse(createFunctionRequest(SAVE_ASSEMBLY_CONFIGURATION, request), onComplete, actions.errorHandler(onError))
        },
        getAssemblyConfiguration: (request: number, onComplete: Dispatch<AssemblyConfiguration>) => {
            requestResponse(createFunctionRequest(GET_ASSEMBLY_CONFIGURATION, request), onComplete, actions.errorHandler())
        },
        getAssembly: (request: number, onComplete: Dispatch<Assembly>) => {
            requestResponse(createFunctionRequest(GET_ASSEMBLY, request), onComplete, actions.errorHandler())
        },
        deleteAssembly: (request: number) => {
            requestResponse(createFunctionRequest(DELETE_ASSEMBLY, request))
        },
        rebuildProject: (request: number) => {
            requestResponse(createFunctionRequest(REBUILD_PROJECT, request))
        },
        getAssemblies: (onComplete: Dispatch<Assembly[]>) => {
            requestResponse(
                createFunctionRequest(GET_ASSEMBLIES),
                assemblies => onComplete(assemblies.filter(assembly => platform.user()?.availableProjects?.includes(assembly.projectId))),
                actions.errorHandler())
        },
        getFilteredAssemblies: (request: AssemblyFilterCriteria, onComplete: Dispatch<AssemblyInformation[]>) => {
            requestResponse(
                createFunctionRequest(GET_FILTERED_ASSEMBLIES, request),
                assemblies => onComplete(assemblies.filter(assembly => platform.user()?.admin || platform.user()?.availableProjects?.includes(assembly.projectId))),
                actions.errorHandler()
            )
        },
        subscribeOnAssembly: (onUpdate: Dispatch<StreamEvent<Assembly>>) => {
            return infinityRequestStream(
                createFunctionRequest(SUBSCRIBE_ON_ASSEMBLY),
                event => (platform.user()?.admin ||  platform.user()?.availableProjects?.includes(event.data.projectId)) && onUpdate(event),
                actions.errorHandler
            );
        },
        cancelAssembly: (request: number) => {
            requestResponse(createFunctionRequest(CANCEL_ASSEMBLY, request), doNothing, actions.errorHandler())
        },
    };
};
