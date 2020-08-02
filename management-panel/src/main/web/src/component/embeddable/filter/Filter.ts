import {Dispatch} from "react";
import {Widget} from "../../../framework/widgets/Widget";
import {Project} from "../../../model/ProjectTypes";
import {ModuleInformation} from "../../../model/ModuleTypes";
import {moduleSearcher, projectSearcher, stringSearcher} from "../common/PlatformSearchers";
import {conditional} from "../../../framework/pattern/Conditional";
import {horizontalGrid, verticalGrid} from "../../../framework/dsl/managed/ManagedGrid";
import {Configurable} from "../../../framework/pattern/Configurable";
import {label} from "../../../framework/dsl/managed/ManagedLabel";
import {checkbox, ManagedCheckbox} from "../../../framework/dsl/managed/ManagedCheckbox";

export type FilteringState = {
    state: string,
    label: string,
    color: string
};

export type FilteringStateGroup = FilteringState[];

type Properties = {
    projects?: Project[]
    versions?: string[]
    names?: string[]
    profiles?: string[]
    modules?: ModuleInformation[]
    states?: FilteringStateGroup[]

    filterProjects?: boolean
    filterProfiles?: boolean
    filterVersions?: boolean
    filterNames?: boolean
    filterModules?: boolean
    filterStates?: boolean
}

class Configuration extends Configurable<Properties> {
    projects = this.property(this.defaultProperties.projects)
    versions = this.property(this.defaultProperties.versions)
    names = this.property(this.defaultProperties.names)
    profiles = this.property(this.defaultProperties.profiles)
    modules = this.property(this.defaultProperties.modules)
    states = this.property(this.defaultProperties.states)
    filteredStates = this.property<string[]>([])
}

export class Filter extends Widget<Filter, Properties, Configuration> {
    #states = new Map<string, ManagedCheckbox>()

    #projectSearcher = projectSearcher({projects: this.configuration.projects.value || []})
    .apply(searcher => {
        this.configuration.projects.consume(searcher.setAvailableValues)
        return searcher;
    });

    #projectsFilter = conditional(() => this.properties.filterProjects)
    .persist(() => this.#projectSearcher)

    #versionSearcher = stringSearcher({strings: this.configuration.versions.value || [], label: "Версии"})
    .apply(searcher => {
        this.configuration.versions.consume(searcher.setAvailableValues)
        return searcher;
    });

    #versionsFilter = conditional(() => this.properties.filterVersions)
    .persist(() => this.#versionSearcher)

    #nameSearcher = stringSearcher({strings: this.configuration.names.value || [], label: "Имена"})
    .apply(searcher => {
        this.configuration.names.consume(searcher.setAvailableValues)
        return searcher;
    });

    #namesFilter = conditional(() => this.properties.filterNames)
    .persist(() => this.#nameSearcher)

    #profileSearcher = stringSearcher({strings: this.configuration.profiles.value || [], label: "Профили"})
    .apply(searcher => {
        this.configuration.profiles.consume(searcher.setAvailableValues)
        return searcher;
    });

    #profilesFilter = conditional(() => this.properties.filterProfiles)
    .persist(() => this.#profileSearcher)

    #modulesSearcher = moduleSearcher({modules: this.configuration.modules.value || []})
    .apply(searcher => {
        this.configuration.modules.consume(searcher.setAvailableValues)
        return searcher;
    });

    #modulesFilter = conditional(() => this.properties.filterModules)
    .persist(() => this.#modulesSearcher)

    #statesFilter = conditional(() => this.properties.filterStates)
    .widget(() => horizontalGrid({justify: "space-between"}).pushWidgets(this.configuration.states.value?.flatMap(stateGroup => stateGroup)
        .map(state => {
            const stateCheckbox = this.#states.get(state.state) || checkbox({style: {color: state.color}})
            .onCheck(() => this.configuration.filteredStates.set(Array.from(this.#states.filterValues(checkbox => checkbox.checked()).keys())));

            this.#states.set(state.state, stateCheckbox)

            return horizontalGrid({spacing: 1, alignItems: "center", wrap: "nowrap"})
            .pushWidget(stateCheckbox)
            .pushWidget(label({noWrap: true, text: state.label}));
        }) || [])
    );

    onProjectsFiltered = (action: Dispatch<Project[]>) => {
        this.#projectSearcher.apply(searcher => searcher.onSelect(action));
        return this;
    }

    onVersionsFiltered = (action: Dispatch<string[]>) => {
        this.#versionSearcher?.apply(searcher => searcher.onSelect(action));
        return this;
    }

    onNamesFiltered = (action: Dispatch<string[]>) => {
        this.#nameSearcher?.apply(searcher => searcher.onSelect(action));
        return this;
    }

    onProfilesFiltered = (action: Dispatch<string[]>) => {
        this.#profileSearcher?.apply(searcher => searcher.onSelect(action));
        return this;
    }

    onModulesFiltered = (action: Dispatch<ModuleInformation[]>) => {
        this.#modulesSearcher?.apply(searcher => searcher.onSelect(action));
        return this;
    }

    onStatesFiltered = (action: Dispatch<string[]>) => {
        this.configuration.filteredStates.consume(action)
        return this;
    }


    filteredVersions = () => this.#versionSearcher.evaluated()
        ? this.#versionSearcher.get().selected()
        : []

    availableVersions = () => this.#versionSearcher.evaluated()
        ? this.#versionSearcher.get().availableValues()
        : []

    filteredNames = () => this.#nameSearcher.evaluated()
        ? this.#nameSearcher.get().selected()
        : []

    availableNames = () => this.#nameSearcher.evaluated()
        ? this.#nameSearcher.get().availableValues()
        : []

    filteredProfiles = () => this.#profileSearcher.evaluated()
        ? this.#profileSearcher.get().selected()
        : []

    availableProfiles = () => this.#profileSearcher.evaluated()
        ? this.#profileSearcher.get().availableValues()
        : []

    filteredProjects = () => this.#projectSearcher.evaluated()
        ? this.#projectSearcher.get().selected()
        : []

    availableProjects = () => this.#projectSearcher.evaluated()
        ? this.#projectSearcher.get().availableValues()
        : []

    filteredModules = () => this.#modulesSearcher.evaluated()
        ? this.#modulesSearcher.get().selected()
        : []

    availableModules = () => this.#modulesSearcher.evaluated()
        ? this.#modulesSearcher.get().availableValues()
        : []

    filteredStates = () => this.configuration.filteredStates.value

    availableStates = () => this.configuration.states.value;


    setAvailableProjects = (projects: Project[]) => {
        this.configuration.projects.set(projects)
        return this;
    }

    setAvailableNames = (names: string[]) => {
        this.configuration.names.set(names)
        return this;
    }

    setAvailableProfiles = (profiles: string[]) => {
        this.configuration.profiles.set(profiles)
        return this;
    }

    setAvailableVersions = (versions: string[]) => {
        this.configuration.versions.set(versions)
        return this;
    }

    setAvailableModules = (modules: ModuleInformation[]) => {
        this.configuration.modules.set(modules)
        return this;
    }


    #filter = verticalGrid({wrap: "nowrap"}).pushWidget(verticalGrid({spacing: 1, wrap: "nowrap"})
        .pushWidget(this.#projectsFilter)
        .pushWidget(this.#versionsFilter)
        .pushWidget(this.#namesFilter)
        .pushWidget(this.#profilesFilter)
        .pushWidget(this.#modulesFilter)
        .pushWidget(this.#statesFilter)
    )

    draw = this.#filter.render;
}

export const filter = (properties: Properties) => new Filter(properties, Configuration);
