import {ALL_RESOURCES, ARTIFACTS_RESOURCE, GIT_RESOURCE, OPEN_SHIFT_RESOURCE, PLATFORM_RESOURCE, PROXY_RESOURCE} from "../constants/ResourceConstants";
import {Dispatch} from "react";
import {ResourceIdentifier, Resources} from "../model/ResourceTypes";
import {useResourceApi} from "../api/ResourceApi";

export class ResourcesLoader {
    #api: typeof useResourceApi;
    #loaded = false;
    #resources = {
        openShift: [],
        git: [],
        artifacts: [],
        platform: [],
        proxy: [],
    } as Resources;

    constructor(api: typeof useResourceApi) {
        this.#api = api;
    }

    get get() {
        return this.#resources;
    }

    get loaded() {
        return this.#loaded;
    }

    ids = (types: string[] = ALL_RESOURCES) => {
        let ids: ResourceIdentifier[] = [];
        for (let type of types) {
            switch (type) {
                case OPEN_SHIFT_RESOURCE:
                    ids = [
                        ...ids,
                        ...this.#resources.openShift.map(resource => ({
                            id: resource.id,
                            type: OPEN_SHIFT_RESOURCE,
                            name: resource.name
                        }))
                    ];
                    break;
                case GIT_RESOURCE:
                    ids = [
                        ...ids,
                        ...this.#resources.git.map(resource => ({
                            id: resource.id,
                            type: GIT_RESOURCE,
                            name: resource.name
                        }))
                    ];
                    break;
                case ARTIFACTS_RESOURCE:
                    ids = [
                        ...ids,
                        ...this.#resources.artifacts.map(resource => ({
                            id: resource.id,
                            type: ARTIFACTS_RESOURCE,
                            name: resource.name
                        }))
                    ];
                    break;
                case PLATFORM_RESOURCE:
                    ids = [
                        ...ids,
                        ...this.#resources.platform.map(resource => ({
                            id: resource.id,
                            type: PLATFORM_RESOURCE,
                            name: resource.name
                        }))
                    ];
                    break;
                case PROXY_RESOURCE:
                    ids = [
                        ...ids,
                        ...this.#resources.proxy.map(resource => ({
                            id: resource.id,
                            type: PROXY_RESOURCE,
                            name: resource.name
                        }))
                    ];
                    break;
            }
        }
        return ids;
    };

    load = (completed?: Dispatch<ResourcesStore>) => {
        this.#loaded = false;
        this.#resources.artifacts = [];
        this.#resources.platform = [];
        this.#resources.git = [];
        this.#resources.openShift = [];
        this.#resources.proxy = [];
        let count = 0;
        const increment = () => {
            if (++count == ALL_RESOURCES.length) {
                this.#loaded = true;
                completed?.(this.store())
            }
        };
        for (let type of ALL_RESOURCES) {
            switch (type) {
                case OPEN_SHIFT_RESOURCE:
                    this.#api().getOpenShiftResources(loaded => {
                        this.#resources.openShift = loaded;
                        increment();
                    });
                    break;
                case GIT_RESOURCE:
                    this.#api().getGitResources(loaded => {
                        this.#resources.git = loaded;
                        increment();
                    });
                    break;
                case ARTIFACTS_RESOURCE:
                    this.#api().getArtifactsResources(loaded => {
                        this.#resources.artifacts = loaded;
                        increment();
                    });
                    break;
                case PLATFORM_RESOURCE:
                    this.#api().getPlatformResources(loaded => {
                        this.#resources.platform = loaded;
                        increment();
                    });
                    break;
                case PROXY_RESOURCE:
                    this.#api().getProxyResources(loaded => {
                        this.#resources.proxy = loaded;
                        increment();
                    });
                    break;
            }
        }
    }

    store = () => {
        if (!this.#loaded) {
            throw new Error("Resource not loaded")
        }
        return resourceStore(this.ids(), this.#resources)
    }
}

export class ResourcesStore {
    #ids: ResourceIdentifier[]
    #resources: Resources;

    get ids() {
        return this.#ids;
    }

    get get() {
        return this.#resources;
    }

    idsOf = (types: string[]) => this.#ids.filter(id => types.includes(id.type))

    resourcesOf = (type: string) => {
        switch (type) {
            case OPEN_SHIFT_RESOURCE:
                return this.#resources.openShift;
            case GIT_RESOURCE:
                return this.#resources.git;
            case ARTIFACTS_RESOURCE:
                return this.#resources.artifacts;
            case PLATFORM_RESOURCE:
                return this.#resources.platform;
            case PROXY_RESOURCE:
                return this.#resources.proxy;
        }
    }

    constructor(ids: ResourceIdentifier[], resource: Resources) {
        this.#ids = ids;
        this.#resources = resource;
    }
}

export const resourcesLoader = (api: typeof useResourceApi) => new ResourcesLoader(api);

export const resourceStore = (ids: ResourceIdentifier[], resources: Resources) => new ResourcesStore(ids, resources);
