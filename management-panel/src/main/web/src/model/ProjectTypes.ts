import {ExternalIdentifier} from "./ExternalTypes";
import {ProxyResource, ResourceIdentifier} from "./ResourceTypes";

export type ProjectVersion = {
    reference: string
    version: string
}

export type ProjectArtifact = {
    name: string
    technologies: string[];
    versions: ProjectVersion[];
}

export type AssembledProjectArtifactsRequest = {
    projectId: number
    version?: string;
}

export type ProjectOpenShiftConfiguration = {
    platformPodsNodeSelector?: OpenShiftLabel[]
}
export type ProjectNotificationsConfiguration = {
    url: string
    additionalMessage: string
    proxyId?: ResourceIdentifier
}

export type Project = {
    id: number
    name: string
    externalId: ExternalIdentifier
    gitResourceId: ResourceIdentifier
    technologies: string[]
    state: string
    creationTimeStamp: number
    versions: ProjectVersion[]
    artifacts: ProjectArtifact[]
    openShiftConfiguration?: ProjectOpenShiftConfiguration
    notificationsConfiguration?: ProjectNotificationsConfiguration
}

export type ProjectRequest = {
    name: string
    gitResourceId: number
    initializationResourceId?: ResourceIdentifier
    openShiftConfiguration?: ProjectOpenShiftConfiguration
    notificationsConfiguration?: ProjectNotificationsConfiguration
}

export type ProjectUpdateRequest = {
    id: number
    name: string
    openShiftConfiguration?: ProjectOpenShiftConfiguration
    notificationsConfiguration?: ProjectNotificationsConfiguration
}
