export type UserRegistrationRequest = {
    name: string
    fullName: string
    email: string
    password: string
}

export type UserAuthorizationRequest = {
    name: string
    password: string
}

export type User = {
    id: number
    name: string
    fullName: string
    password: Uint8Array
    email: string
    token: string
    updateTimeStamp: number
    admin?: boolean
    availableProjects: number[]
    availableActions: string[]
}
