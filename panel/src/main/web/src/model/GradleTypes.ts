import {Property} from "./Property";

export type GradleCacheConfiguration = {
    serverUrlProperty?: string
}

export type GradleAssemblyConfiguration = {
    arguments?: string
    version?: string
    jdkVersion?: string
    initScriptGroovyContent?: string
    initScriptKotlinContent?: string
    initScriptFormat?: string
    properties?: Property[]
    cacheConfiguration?: GradleCacheConfiguration
}

export type GradleArtifactConfiguration = {
    arguments?: string
}
