import {ResourceIdentifier} from "./ResourceTypes";
import {ApplicationIdentifier} from "./ApplicationTypes";

export type FilebeatApplication = {
    id: number
    name: string
    url: string
    resourceId: ResourceIdentifier
}

export type FilebeatApplicationRequest = {
    name: string
    url: string
    resourceId: ResourceIdentifier
}

export type FilebeatModuleApplication = {
    applicationId: ApplicationIdentifier
    port: number
    url: string
    resourceId: ResourceIdentifier
    configuration: string
}
