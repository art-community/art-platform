import {ResourceIdentifier} from "./ResourceTypes";

export type NetworkAccessRequest = {
    resourceId: ResourceIdentifier
    hostName: string
    port: number
    timeout: number
    openShiftPodConfiguration?: OpenShiftPodConfiguration
}
