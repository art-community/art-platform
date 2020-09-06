import {Dispatch} from "react";
import {useAssemblyApi} from "../api/AssemblyApi";
import {AssemblyInformation} from "../model/AssemblyTypes";
import {MAX_ASSEMBLIES} from "../constants/AssemblyConstants";

export class AssembliesLoader {
    #api: typeof useAssemblyApi;
    #loaded = false;
    #assemblies: AssemblyInformation[] = [];

    constructor(api: typeof useAssemblyApi) {
        this.#api = api;
    }

    get get() {
        return this.#assemblies;
    }

    get loaded() {
        return this.#loaded;
    }

    load = (completed?: Dispatch<AssemblyInformation[]>) => {
        this.#loaded = false;
        this.#assemblies = [];
        this.#api().getFilteredAssemblies({count: MAX_ASSEMBLIES, sorted: true}, loaded => {
            this.#assemblies = loaded;
            this.#loaded = true;
            completed?.(this.#assemblies);
        });
    }
}

export const assembliesLoader = (api: typeof useAssemblyApi) => new AssembliesLoader(api);
