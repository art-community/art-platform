import {Dispatch} from "react";
import {PreparedConfigurationIdentifier} from "../model/PreparedConfigurationTypes";
import {usePreparedConfigurationApi} from "../api/PreparedConfigurationApi";

export class PreparedConfigurationsLoader {
    #api: typeof usePreparedConfigurationApi;
    #loaded = false;
    #configurations: PreparedConfigurationIdentifier[] = [];

    constructor(api: typeof usePreparedConfigurationApi) {
        this.#api = api;
    }

    get get() {
        return this.#configurations;
    }

    get loaded() {
        return this.#loaded;
    }

    load = (completed?: Dispatch<PreparedConfigurationIdentifier[]>) => {
        this.#loaded = false;
        this.#configurations = [];
        this.#api().getPreparedConfigurationIds(loaded => {
            this.#configurations = loaded;
            this.#loaded = true;
            completed?.(this.#configurations);
        });
    }
}

export const preparedConfigurationsLoader = (api: typeof usePreparedConfigurationApi) => new PreparedConfigurationsLoader(api);
