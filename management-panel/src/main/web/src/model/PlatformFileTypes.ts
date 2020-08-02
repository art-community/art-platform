export type PlatformFileIdentifier = {
    id: number
    name: string
}

export type PlatformFile = {
    id: number
    name: string
    bytes: Uint8Array
}

export type PlatformFilePayload = {
    id: PlatformFileIdentifier
    bytes: Buffer
}

export type PlatformFileChunk = {
    id: PlatformFileIdentifier
    size: number
    bytes: Uint8Array
}

export type PlatformFileCloneRequest = {
    currentFileId: PlatformFileIdentifier
    newFileId: PlatformFileIdentifier
}

export type StringFile = {
    name: string
    content: string
}
