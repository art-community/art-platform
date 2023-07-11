import {createFunctionRequest, infinityRequestStream, PlatformClient, requestResponse} from "../client/PlatformClient";
import {
    ADD_EXTERNAL_ARTIFACTS,
    ADD_PROJECT,
    DELETE_PROJECT,
    GET_ASSEMBLED_PROJECT_ARTIFACTS,
    GET_PROJECT,
    GET_PROJECTS,
    RELOAD_PROJECT,
    SUBSCRIBE_ON_PROJECT,
    UPDATE_PROJECT
} from "../constants/ApiConstants";
import {Dispatch} from "react";
import {useApiActions} from "./ApiActions";
import {StreamEvent} from "../framework/pattern/Stream";
import {AssembledProjectArtifactsRequest, Project, ProjectUpdateRequest, ProjectRequest} from "../model/ProjectTypes";
import {AssembledArtifact} from "../model/AssemblyTypes";
import {ExternalArtifactsRequest} from "../model/ExternalTypes";
import {doNothing} from "../framework/constants/Constants";
import {platform} from "../component/entry/EntryPoint";

export const useProjectApi = () => {
    const actions = useApiActions();

    return {
        addProject: (request: ProjectRequest) => {
            requestResponse(createFunctionRequest(ADD_PROJECT, request), doNothing, actions.errorHandler())
        },
        updateProject: (request: ProjectUpdateRequest) => {
            requestResponse(createFunctionRequest(UPDATE_PROJECT, request), doNothing, actions.errorHandler())
        },
        getProjects: (onComplete: Dispatch<Project[]>, client: PlatformClient = PlatformClient.platformClient()) => {
            client.requestResponse(
                createFunctionRequest(GET_PROJECTS),
                projects => onComplete(projects.filter(project => platform.user()?.admin || platform.user()?.availableProjects?.includes(project.id))),
                actions.errorHandler()
            )
        },
        getProjectNames: (onComplete: Dispatch<string[]>, client: PlatformClient = PlatformClient.platformClient()) => {
            client.requestResponse(createFunctionRequest(GET_PROJECTS), projects => onComplete(projects.map(project => project.name)), actions.errorHandler())
        },
        getAssembledProjectArtifacts: (request: AssembledProjectArtifactsRequest, onComplete: Dispatch<AssembledArtifact[]>) => {
            requestResponse(createFunctionRequest(GET_ASSEMBLED_PROJECT_ARTIFACTS, request), onComplete, actions.errorHandler())
        },
        getProject: (id: number, onComplete: Dispatch<Project>) => {
            requestResponse(createFunctionRequest(GET_PROJECT, id), onComplete, actions.errorHandler())
        },
        reloadProject: (id: number) => {
            requestResponse(createFunctionRequest(RELOAD_PROJECT, id), doNothing, actions.errorHandler())
        },
        deleteProject: (id: number) => {
            requestResponse(createFunctionRequest(DELETE_PROJECT, id))
        },
        addExternalArtifact: (request: ExternalArtifactsRequest, client: PlatformClient) => {
            client.requestResponse(createFunctionRequest(ADD_EXTERNAL_ARTIFACTS, request))
        },
        subscribeOnProject: (onUpdate: Dispatch<StreamEvent<Project>>) => {
            return infinityRequestStream(createFunctionRequest(SUBSCRIBE_ON_PROJECT), onUpdate, actions.errorHandler());
        }
    };
};
