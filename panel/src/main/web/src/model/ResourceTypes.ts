export type ResourceIdentifier = {
    id: number
    name: string
    type: string
}

export type OpenShiftResource = {
    id: number
    name: string
    apiUrl: string
    applicationsDomain: string
    privateRegistryUrl: string
    userName: string
    password: string
}

export type PlatformResource = {
    id: number
    name: string
    url: string
    userName: string
    password: string
}

export type ArtifactsResource = {
    id: number
    name: string
    url: string
    userName: string
    password: string
}

export type GitResource = {
    id: number
    name: string
    url: string
    userName: string
    password: string
}

export type ProxyResource = {
    id: number
    name: string
    host: string
    port: number
    userName: string
    password: string
}


export type OpenShiftResourceRequest = {
    name: string
    apiUrl: string
    applicationsDomain: string
    userName: string
    password: string
}

export type ArtifactsResourceRequest = {
    name: string
    url: string
    userName: string
    password: string
}

export type GitResourceRequest = {
    name: string
    url: string
    userName: string
    password: string
}

export type ProxyResourceRequest = {
    name: string
    host: string
    port: number
    userName: string
    password: string
}

export type Resources = {
    openShift: OpenShiftResource[]
    artifacts: ArtifactsResource[]
    git: GitResource[]
    platform: PlatformResource[]
    proxy: ProxyResource[]
}
