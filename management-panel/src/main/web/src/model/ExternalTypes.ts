import {ArtifactsResource, OpenShiftResource, ResourceIdentifier} from "./ResourceTypes";

export type ExternalArtifactsRequest = {
    artifacts: ExternalArtifact[]
    openShiftResources: OpenShiftResource[]
    artifactsResources: ArtifactsResource[]
}

export type ExternalArtifact = {
    name: string
    version: string
    externalId: ExternalIdentifier
    projectId: number
}

export type ExternalIdentifier = {
    id: string
    resourceId: ResourceIdentifier
}