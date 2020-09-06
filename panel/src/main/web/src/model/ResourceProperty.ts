import {ResourceIdentifier} from "./ResourceTypes";

export type ResourceProperty = {
    value?: string
    name?: string
    resourceId?: ResourceIdentifier;
}

export type ResourceAttribute = {
    name: string
    value: string
    isPassword?: boolean
}
