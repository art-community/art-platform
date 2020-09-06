import {Dispatch} from "react";
import {useModuleApi} from "../api/ModuleApi";
import {ModuleInformation} from "../model/ModuleTypes";

export class ModulesLoader {
    #api: typeof useModuleApi;
    #loaded = false;
    #modules: ModuleInformation[] = [];

    constructor(api: typeof useModuleApi) {
        this.#api = api;
    }

    get get() {
        return this.#modules;
    }

    get loaded() {
        return this.#loaded;
    }

    load = (completed?: Dispatch<ModuleInformation[]>) => {
        this.#loaded = false;
        this.#modules = [];
        this.#api().getFilteredModules({sorted: true}, loaded => {
            this.#modules = loaded;
            this.#loaded = true;
            completed?.(this.#modules);
        });
    }
}

export const modulesLoader = (api: typeof useModuleApi) => new ModulesLoader(api);
