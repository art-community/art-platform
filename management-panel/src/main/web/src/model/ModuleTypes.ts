import {AssembledArtifact} from "./AssemblyTypes";
import {ApplicationIdentifier} from "./ApplicationTypes";
import {ExternalIdentifier} from "./ExternalTypes";
import {ResourceIdentifier} from "./ResourceTypes";
import {PlatformFileIdentifier, StringFile} from "./PlatformFileTypes";
import {PreparedConfigurationIdentifier} from "./PreparedConfigurationTypes";

export type PortMapping = {
    internalPort: number
    externalPort: number
}

export type ModuleUrl = {
    url?: string
    port?: number
}

export type ModuleConfigurationDraft = {
    resourceId: ResourceIdentifier
    artifact: AssembledArtifact
    name?: string
    url?: ModuleUrl
    parameters?: string
    count: number
    ports?: number[];
    preparedConfigurations?: PreparedConfigurationIdentifier[]
    manualConfigurations?: StringFile[]
    additionalFiles?: ModuleFile[]
    applications?: ModuleApplication[]
    hasParameters?: boolean
    hasUrl?: boolean
    hasPorts?: boolean
    hasStringConfigurations?: boolean
}

export type ModuleConfiguration = {
    resourceId: ResourceIdentifier
    artifact: AssembledArtifact
    name?: string
    url?: ModuleUrl
    parameters?: string
    count: number
    ports?: number[];
    preparedConfigurations?: PreparedConfigurationIdentifier[]
    manualConfigurations?: StringFile[]
    additionalFiles?: PlatformFileIdentifier[]
    applications?: ModuleApplication[]
}

export type ModuleInformation = {
    id: number
    name: string
    projectId: number
    resourceId: ResourceIdentifier
    internalIp: string
    externalId: ExternalIdentifier
    artifact: AssembledArtifact
    parameters?: string
    url?: ModuleUrl
    count: number
    ports?: number[];
    portMappings?: PortMapping[];
    preparedConfigurations?: PreparedConfigurationIdentifier[]
    manualConfigurations?: StringFile[]
    additionalFiles?: PlatformFileIdentifier[]
    state: string
    updateTimeStamp?: number
}

export type Module = ModuleInformation & {
    applications?: ModuleApplication[]
}

export type  ModuleApplication = {
    applicationId: ApplicationIdentifier
    application: any
}

export type ModuleFilterCriteria = {
    projectIds?: number[]
    states?: string[]
    ids?: number[]
    versions?: string[]
    sorted: boolean
}

export type ModuleFile = {
    name: string
    bytes: Buffer
}

export type ModuleInstallationRequest = {
    projectId: number
    configuration: ModuleConfiguration
}

export type ModuleUpdateRequest = {
    moduleId: number
    newModuleConfiguration: ModuleConfiguration
}

export type UpdateModulesVersionRequest = {
    ids: number[]
    version: string
}
