import {Dispatch} from "react";
import {useApplicationApi} from "../api/ApplicationsApi";
import {ApplicationIdentifier, Applications} from "../model/ApplicationTypes";
import {ALL_APPLICATIONS, FILEBEAT_APPLICATION} from "../constants/ApplicationConstants";
import {FilebeatApplication} from "../model/FilebeatTypes";

export class ApplicationsLoader {
    #api: typeof useApplicationApi;
    #loaded = false;
    #applications = {
        filebeat: []
    } as Applications;

    constructor(api: typeof useApplicationApi) {
        this.#api = api;
    }

    get get() {
        return this.#applications;
    }

    get loaded() {
        return this.#loaded;
    }

    ids = (types: string[] = ALL_APPLICATIONS) => {
        let ids: ApplicationIdentifier[] = [];
        for (let type of types) {
            switch (type) {
                case FILEBEAT_APPLICATION:
                    ids = [
                        ...ids,
                        ...this.#applications.filebeat.map(application => ({
                            id: application.id,
                            type: FILEBEAT_APPLICATION,
                            name: application.name
                        }))
                    ];
                    break;
            }
        }
        return ids;
    };

    load = (completed?: Dispatch<ApplicationsStore>) => {
        this.#loaded = false;
        this.#applications.filebeat = [];
        let count = 0;
        const increment = () => {
            if (++count == ALL_APPLICATIONS.length) {
                this.#loaded = true;
                completed?.(this.store())
            }
        };
        for (let type of ALL_APPLICATIONS) {
            switch (type) {
                case FILEBEAT_APPLICATION:
                    this.#api().getFilebeatApplications(loaded => {
                        this.#applications.filebeat = loaded;
                        increment();
                    });
                    break;
            }
        }
    }

    store = () => {
        if (!this.#loaded) {
            throw new Error("Applications not loaded")
        }
        return applicationStore(this.ids(), this.#applications)
    }
}

export class ApplicationsStore {
    #ids: ApplicationIdentifier[]
    #applications: Applications;

    get ids() {
        return this.#ids;
    }

    get get() {
        return this.#applications;
    }

    idsOf = (types: string[]) => this.#ids.filter(id => types.includes(id.type))

    applicationsOf = (type: string) => {
        switch (type) {
            case FILEBEAT_APPLICATION:
                return this.#applications.filebeat;
        }
        return [];
    }

    filebeatOf = (id: ApplicationIdentifier) => this.applicationsOf(FILEBEAT_APPLICATION).find(filebeat => filebeat.id == id.id) as FilebeatApplication

    constructor(ids: ApplicationIdentifier[], applications: Applications) {
        this.#ids = ids;
        this.#applications = applications;
    }
}

export const applicationsLoader = (api: typeof useApplicationApi) => new ApplicationsLoader(api);

export const applicationStore = (ids: ApplicationIdentifier[], applications: Applications) => new ApplicationsStore(ids, applications);
