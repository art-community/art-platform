export type PreparedConfigurationRequest = {
    projectId: number
    profile: string
    name: string
    configuration: string
}

export type PreparedConfigurationFilterCriteria = {
    projectIds: number[]
    profiles: string[]
    names: string[]
}

export type PreparedConfigurationIdentifier = {
    id: number
    projectId: number
    profile: string
    name: string
}

export type PreparedConfiguration = {
    id: number
    projectId: number
    profile: string
    name: string
    configuration: string
}
