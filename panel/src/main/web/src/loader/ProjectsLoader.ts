import {Dispatch} from "react";
import {useProjectApi} from "../api/ProjectApi";
import {Project} from "../model/ProjectTypes";
import {PROJECT_INITIALIZED_STATE} from "../constants/States";

export class ProjectsLoader {
    #api: typeof useProjectApi;
    #loaded = false;
    #projects: Project[] = [];

    constructor(api: typeof useProjectApi) {
        this.#api = api;
    }

    get get() {
        return this.#projects;
    }

    get getInitialized() {
        return this.#projects.filter(project => project.state == PROJECT_INITIALIZED_STATE);
    }

    get loaded() {
        return this.#loaded;
    }

    load = (completed?: Dispatch<Project[]>) => {
        this.#loaded = false;
        this.#projects = [];
        this.#api().getProjects(loaded => {
            this.#projects = loaded;
            this.#loaded = true;
            completed?.(this.#projects);
        });
    }
}

export const projectsLoader = (api: typeof useProjectApi) => new ProjectsLoader(api);
