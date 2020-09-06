import {createFunctionRequest, requestResponse} from "../client/PlatformClient";
import {Dispatch, DispatchWithoutAction} from "react";
import {
    ADD_ARTIFACTS_RESOURCE,
    ADD_GIT_RESOURCE,
    ADD_OPEN_SHIFT_RESOURCE, ADD_PROXY_RESOURCE,
    DELETE_ARTIFACTS_RESOURCE,
    DELETE_GIT_RESOURCE,
    DELETE_OPEN_SHIFT_RESOURCE, DELETE_PROXY_RESOURCE,
    GET_ARTIFACTS_RESOURCE,
    GET_ARTIFACTS_RESOURCES,
    GET_GIT_RESOURCE,
    GET_GIT_RESOURCES,
    GET_OPEN_SHIFT_RESOURCE,
    GET_OPEN_SHIFT_RESOURCES,
    GET_PLATFORM_RESOURCES, GET_PROXY_RESOURCE, GET_PROXY_RESOURCES,
    GET_RESOURCES_IDS,
    UPDATE_ARTIFACTS_RESOURCE,
    UPDATE_GIT_RESOURCE,
    UPDATE_OPEN_SHIFT_RESOURCE, UPDATE_PROXY_RESOURCE
} from "../constants/ApiConstants";
import {useApiActions} from "./ApiActions";
import {
    ArtifactsResource,
    ArtifactsResourceRequest,
    GitResource,
    GitResourceRequest,
    OpenShiftResource,
    OpenShiftResourceRequest,
    PlatformResource, ProxyResource,
    ProxyResourceRequest,
    ResourceIdentifier
} from "../model/ResourceTypes";

export const useResourceApi = () => {
    const actions = useApiActions();

    return {
        addOpenShiftResource: (request: OpenShiftResourceRequest, onComplete: DispatchWithoutAction) => {
            requestResponse(createFunctionRequest(ADD_OPEN_SHIFT_RESOURCE, request), onComplete, actions.errorHandler())
        },
        addArtifactsResource: (request: ArtifactsResourceRequest, onComplete: DispatchWithoutAction) => {
            requestResponse(createFunctionRequest(ADD_ARTIFACTS_RESOURCE, request), onComplete, actions.errorHandler())
        },
        addGitResource: (request: GitResourceRequest, onComplete: DispatchWithoutAction) => {
            requestResponse(createFunctionRequest(ADD_GIT_RESOURCE, request), onComplete, actions.errorHandler())
        },
        addProxyResource: (request: ProxyResourceRequest, onComplete: DispatchWithoutAction) => {
            requestResponse(createFunctionRequest(ADD_PROXY_RESOURCE, request), onComplete, actions.errorHandler())
        },

        getOpenShiftResources: (onComplete: Dispatch<OpenShiftResource[]>) => {
            requestResponse(createFunctionRequest(GET_OPEN_SHIFT_RESOURCES), onComplete, actions.errorHandler())
        },
        getProxyResources: (onComplete: Dispatch<ProxyResource[]>) => {
            requestResponse(createFunctionRequest(GET_PROXY_RESOURCES), onComplete, actions.errorHandler())
        },
        getPlatformResources: (onComplete: Dispatch<PlatformResource[]>) => {
            requestResponse(createFunctionRequest(GET_PLATFORM_RESOURCES), onComplete, actions.errorHandler())
        },
        getArtifactsResources: (onComplete: Dispatch<ArtifactsResource[]>) => {
            requestResponse(createFunctionRequest(GET_ARTIFACTS_RESOURCES), onComplete, actions.errorHandler())
        },
        getGitResources: (onComplete: Dispatch<GitResource[]>) => {
            requestResponse(createFunctionRequest(GET_GIT_RESOURCES), onComplete, actions.errorHandler())
        },

        getOpenShiftResource: (id: number, onComplete: Dispatch<OpenShiftResource>) => {
            requestResponse(createFunctionRequest(GET_OPEN_SHIFT_RESOURCE, id), onComplete, actions.errorHandler())
        },
        getProxyResource: (id: number, onComplete: Dispatch<ProxyResource>) => {
            requestResponse(createFunctionRequest(GET_PROXY_RESOURCE, id), onComplete, actions.errorHandler())
        },
        getArtifactsResource: (id: number, onComplete: Dispatch<ArtifactsResource>) => {
            requestResponse(createFunctionRequest(GET_ARTIFACTS_RESOURCE, id), onComplete, actions.errorHandler())
        },
        getGitResource: (id: number, onComplete: Dispatch<GitResource>) => {
            requestResponse(createFunctionRequest(GET_GIT_RESOURCE, id), onComplete, actions.errorHandler())
        },

        getResourceIds: (onComplete: Dispatch<ResourceIdentifier[]>) => {
            requestResponse(createFunctionRequest(GET_RESOURCES_IDS), onComplete, actions.errorHandler())
        },

        updateOpenShiftResource: (resource: OpenShiftResource, onComplete: Dispatch<OpenShiftResource>) => {
            requestResponse(createFunctionRequest(UPDATE_OPEN_SHIFT_RESOURCE, resource), onComplete, actions.errorHandler())
        },
        updateArtifactsResource: (resource: ArtifactsResource, onComplete: Dispatch<ArtifactsResource>) => {
            requestResponse(createFunctionRequest(UPDATE_ARTIFACTS_RESOURCE, resource), onComplete, actions.errorHandler())
        },
        updateGitResource: (resource: GitResource, onComplete: Dispatch<GitResource>) => {
            requestResponse(createFunctionRequest(UPDATE_GIT_RESOURCE, resource), onComplete, actions.errorHandler())
        },
        updateProxyResource: (resource: ProxyResource, onComplete: Dispatch<ProxyResource>) => {
            requestResponse(createFunctionRequest(UPDATE_PROXY_RESOURCE, resource), onComplete, actions.errorHandler())
        },

        deleteOpenShiftResource: (id: number, onComplete: Dispatch<OpenShiftResource>) => {
            requestResponse(createFunctionRequest(DELETE_OPEN_SHIFT_RESOURCE, id), onComplete)
        },
        deleteArtifactsResource: (id: number, onComplete: Dispatch<ArtifactsResource>) => {
            requestResponse(createFunctionRequest(DELETE_ARTIFACTS_RESOURCE, id), onComplete)
        },
        deleteGitResource: (id: number, onComplete: Dispatch<GitResource>) => {
            requestResponse(createFunctionRequest(DELETE_GIT_RESOURCE, id), onComplete)
        },
        deleteProxyResource: (id: number, onComplete: Dispatch<ProxyResource>) => {
            requestResponse(createFunctionRequest(DELETE_PROXY_RESOURCE, id), onComplete)
        },
    };
};
