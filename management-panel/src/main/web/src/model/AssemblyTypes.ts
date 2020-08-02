import {ResourceIdentifier} from "./ResourceTypes";
import {GradleArtifactConfiguration, GradleAssemblyConfiguration} from "./GradleTypes";
import {ProjectArtifact, ProjectVersion} from "./ProjectTypes";
import {ExternalIdentifier} from "./ExternalTypes";
import {ASSEMBLY_BUILDING_STATE, ASSEMBLY_RESTARTED_STATE, ASSEMBLY_STARTED_ON_RESOURCE_STATE} from "../constants/States";

export type AssemblyConfiguration = {
    id: number
    artifactConfigurations?: ArtifactConfiguration[]
    technology?: string
    gradleConfiguration?: GradleAssemblyConfiguration
    defaultResourceId: ResourceIdentifier
}

export type ArtifactConfiguration = {
    name: string
    artifact?: ProjectArtifact
    archives: ArtifactArchiveConfiguration[];
    gradleConfiguration?: GradleArtifactConfiguration
}

export type ArtifactArchiveConfiguration = {
    resourceId: ResourceIdentifier;
    archiveTechnology?: string;
    dockerConfiguration?: DockerArchiveConfiguration;
}

export type AssembledArtifact = {
    name: string;
    version: string;
    externalId: ExternalIdentifier;
}

export type AssemblyInformation = {
    id: number
    projectId: number
    technology: string
    version: ProjectVersion
    state: string
    startTimeStamp: number
    endTimeStamp: number
    resourceId: ResourceIdentifier
    logId: number
    artifacts: AssembledArtifact[]
}

export type Assembly = AssemblyInformation & {
    artifactConfigurations: ArtifactConfiguration[]
}

export type AssemblyFilterCriteria = {
    projectIds?: number[];
    states?: string[];
    versions?: string[];
    sorted?: boolean;
    count: number
}

export type BuildRequest = {
    projectId: number
    configurationId: number
    version: ProjectVersion
    resourceId: ResourceIdentifier
    artifactConfigurations: ArtifactConfiguration[]
}
